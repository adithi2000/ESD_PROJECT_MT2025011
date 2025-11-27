// src/utils/http.ts

import axios, { type AxiosInstance, type AxiosError } from 'axios'; // 1
import { type SalaryHistoryDto,type  FacultyProfileDto } from '../model/model';
// 2. Define the base URL for your Spring Boot backend
const BASE_URL = 'http://localhost:8080';

// 3. Create an instance of Axios
const http: AxiosInstance = axios.create({
    baseURL: BASE_URL,
    withCredentials:true,
    headers: {
        'Content-Type': 'application/json', // We send JSON data
    },
});

export const hardResetBrowser = () => {
    // 1. Clear any potentially stored data/session flags
    localStorage.clear();
    sessionStorage.clear();
    // 2. Force a full reload of the application
   // window.location.href ='/login';
    
}
//Response Interceptor: Global error handling
http.interceptors.response.use(
    (response) => response, // 10. Success: Just return the response data
    (error: AxiosError) => {
        // 11. Check for Unauthorized status (Token expired/invalid)
        if (error.response && error.response.status === 401) {
            
            window.alert('Session expired. Please log in again.');

            window.location.href = BASE_URL + '/login'; 
            
            return Promise.reject(error);
        }
        return Promise.reject(error);
    }
);

export const getSalaryHistory = async (): Promise<SalaryHistoryDto[]> => {
    // We use the SECURED 'http' instance here.
    const response = await http.get<SalaryHistoryDto[]>('/api/faculty/salary/history');
    
    // The JWT was automatically added to the request headers by the interceptor.
    // The 401 check is waiting on the response side via the interceptor.
    
    return response.data; 
};

export const logoutProfile = async (): Promise<void> => {
    // We use the SECURED 'http' instance here.
   try {
        await http.post('/api/auth/logout'); // Backend clears the HTTP-only cookie
    } catch (e) {
        console.error("Backend logout endpoint failed, proceeding with client reset:", e);
    } finally {
        hardResetBrowser(); 

        // Client reset (forces reload/redirect)

    }
    
};

export const getFacultyProfile = async (): Promise<FacultyProfileDto> => {
    // We use the SECURED 'http' instance again.
    const response = await http.get<FacultyProfileDto>('/api/faculty/details');
    return response.data;
};

// Inside src/utils/http.ts (Review)

export const downloadPayslip = (payslipId: number): void => {
    const downloadUrl = `http://localhost:8080/api/faculty/salary/download/${payslipId}`;
    
    // 2. Trigger the download using the browser's native window function
    window.open(downloadUrl, '_blank');
};

export default http; // 13. Export the custom Axios instance