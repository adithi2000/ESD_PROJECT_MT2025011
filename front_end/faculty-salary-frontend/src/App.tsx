import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import OauthRedirectHandler from './pages/OauthRedirectHandler';
import React from 'react';
import { useAuth } from './context/AuthContext';
import { SalaryHistoryPage } from './pages/SalaryHistoryPage';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return <div>Loading...</div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return <>{children}</>;
};

const App: React.FC = () => {
  return (
    <>
    <script src="https://cdn.tailwindcss.com"></script>
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/oauth2/redirect" element={<OauthRedirectHandler />} />

      <Route
        path="/salary-history"
        element={
          <ProtectedRoute>
             <SalaryHistoryPage />
          </ProtectedRoute>
        }
      />
    </Routes>
    </>
  );
};

export default App;
