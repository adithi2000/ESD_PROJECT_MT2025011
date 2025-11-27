import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
import { getSalaryHistory, downloadPayslip } from '../../utils/httpUtils'; // 1. Import API functions
import { useAuth } from '../../context/AuthContext'; // 2. Import Auth context
import { type SalaryHistoryDto } from '../../model/model';
import SalaryHistoryPresentation from '../presentation/SalaryHistoryPresentation'; 

const SalaryHistoryContainer: React.FC = () => {
    // 3. State Hooks: Memory for fetched data and status
    const [history, setHistory] = useState<SalaryHistoryDto[]>([]); 
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const { isAuthenticated, logout, userProfile } = useAuth(); // 4. Get global state/actions
    // const navigate = useNavigate();

    // 5. useEffect Hook: Handles the secure data fetching
    useEffect(() => {
        // If the context says we're not authenticated (e.g., token cleared), redirect.
        if (!isAuthenticated) {
             setLoading(false);
             return;
        }

        const fetchSalaryData = async () => {
            try {
                // 6. Call the secured utility function
                const data = await getSalaryHistory(); 
                
                setHistory(data);
                setError(null);
            } catch (err) {
                // 7. Global interceptor handles 401. We just handle general errors here.
                setError('Could not retrieve salary records.');
            } finally {
                setLoading(false);
            }
        };

        fetchSalaryData();
    // 8. Run whenever isAuthenticated changes (after initial check or login)
    }, [isAuthenticated]); 

    // 9. Handler for the download button click
    const handleDownload = (payslipId: number) => { 
        downloadPayslip(payslipId); // Call the utility function
    };

    // 10. Pass all data and handlers to the dumb presentation component
    return (
        <SalaryHistoryPresentation 
            history={history}
            loading={loading}
            error={error}
            profile={userProfile} // Pass profile details for display (name/designation)
            onDownload={handleDownload}
            onLogout={logout} // Pass the global logout function
        />
    );
};

export default SalaryHistoryContainer;