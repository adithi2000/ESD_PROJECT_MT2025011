// Generally AuthContext is used to provide authentication state and methods throughout a React application.
// However, in this project, we handle authentication primarily through HTTP interceptors in httpUtils.ts.
// Therefore, AuthContext.tsx may not be strictly necessary unless you want to manage additional auth state or methods globally.
// You can choose to keep it for future enhancements or remove it if it's not being used.
// AuthContext: Holds the current user, login status, and logout function. (Needed for all apps.)
import React, { createContext, useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { type AuthState, type AuthContextType } from '../model/auth'; // 1
import { type FacultyProfileDto } from '../model/model';
import { getFacultyProfile,logoutProfile,hardResetBrowser } from '../utils/httpUtils'; 

const AuthContext=createContext<AuthContextType | undefined>(undefined);
const initialAuthState:AuthState={
    isAuthenticated:false,
    userProfile:null,
    loading:true,
};

// 5. The Provider Component
export const AuthContextProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
   const navigate = useNavigate();
const [authState,setAuthState]=useState<AuthState>(initialAuthState);
// Function to run immediately after the JWT is captured (in RedirectHandler)
  const login = () => {
    //localStorage.setItem('jwtToken', token);
    fetchProfile(); // Immediately fetch the user's profile
  };

  // Function to clear session data
 const logout_error = useCallback(() => {
    hardResetBrowser();
  //localStorage.removeItem('jwtToken');
  setAuthState(initialAuthState);
  
  navigate('/login', { replace: true });
}, [navigate]);


const logout = useCallback(async () => {
  //localStorage.removeItem('jwtToken');
    await logoutProfile();
  setAuthState(initialAuthState);
  navigate('/login', { replace: true });
}, [navigate]);

const fetchProfile=useCallback(async()=>{
    try{
        const profile:FacultyProfileDto=await getFacultyProfile();
        setAuthState({
            isAuthenticated:true,
            userProfile:profile,
            loading:false,
        });
    }catch(error){
        setAuthState({
            isAuthenticated:false,
            userProfile:null,
            loading:false,
        });
        logout();
    }
},[logout]);

useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        // If token exists, try to fetch the profile to confirm it's still valid
        setAuthState(prev => ({ ...prev, loading: true }));
        fetchProfile();
    } else {
        // No token, finish loading immediately
        setAuthState(prev => ({ ...prev, loading: false }));
    }
  }, [fetchProfile]);

//The final object passed to all consuming components
  const contextValue: AuthContextType = {
    ...authState, // includes isAuthenticated, profile, loading
    login,
    logout,
    fetchProfile,
    logout_error
  };

  return (
    // 10. The Provider provides the data (contextValue) to all child components
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

// 11. Custom Hook for easy consumption (useAuth())
export const useAuth = () => {
  const context = React.useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthContextProvider');
  }
  return context;
};