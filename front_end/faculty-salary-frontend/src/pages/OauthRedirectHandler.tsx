import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // 1. Import the custom hook

/*
Since you need to complete the login flow and then securely fetch data, we will create the files in this order:

The Redirect Handler (Oauth2RedirectHandler.tsx): Captures the JWT and calls the global login function.

The Container (SalaryHistoryContainer.tsx): Fetches the protected salary data using your utility.

The Presentation (SalaryHistoryPresentation.tsx): Renders the final UI. */

const OauthRedirectHandler: React.FC = () => {
  const navigate = useNavigate(); // 1. useNavigate to redirect after login
  const location = useLocation(); // 2. useLocation to access query params
  const { login,logout_error } = useAuth(); // 2. Use the custom hook to get the login function

  // useEffect runs the logic once when the component is loaded
  useEffect(() => {
    const handleAuth = async () => {
      // Get the URL parameters (query string)
      const params = new URLSearchParams(location.search);
      const auth_success = params.get('auth_status'); // Look for the auth_success flag
      const auth_error = params.get('error'); // Look for the auth_error flag
      //const token = params.get('token'); // Look for the JWT string

      if (auth_error && !auth_success) {
        // 4. Handle authentication error
        console.error('Authentication Error:', auth_error);
        logout_error();
        await new Promise(resolve => setTimeout(resolve, 1000));
        //alert(`Authentication Failed: ${auth_error}`);
        //navigate('/login', { replace: true,state: { error: auth_error } });
        navigate(`/login?error=${encodeURIComponent(auth_error)}`, { replace: true });
        
      return;
      }

      if (auth_success && !auth_error) {
        login(); // Call the global login function to update context
        navigate('/salary-history', { replace: true });
        return;
      }

       // 3. FALLBACK
    console.error('Authentication Flow Failed: No status received from the backend.');
     //window.alert(`Authentication Failed: Internal Error occurred during login.`);
    //navigate('/login', { replace: true,state: { error: "Internal Error occurred during login." } });
     navigate(`/login?error=500Internal`, { replace: true });
     
    };

    handleAuth();
  }, [location.search, navigate, login]);
  return (
    <div className="text-center my-5">
      {/* Bootstrap Spinner */}
      <div className="spinner-border text-primary" role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      <p className="mt-2">Completing Secure Login...</p>
    </div>
  );
};

export default OauthRedirectHandler;