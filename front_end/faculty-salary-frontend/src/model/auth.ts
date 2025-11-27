import {type FacultyProfileDto }  from "./model";

// AuthContext: Holds the current user, login status, and logout function. (Needed for all apps.)
export interface AuthState{
    isAuthenticated: boolean;
    userProfile: FacultyProfileDto | null;
    loading:boolean;
}

// 2. Interface for the Context's methods (the actions a component can perform)
export interface AuthContextType extends AuthState{
    login:(token?:string)=>void;
    logout:()=>void;
    fetchProfile:()=>Promise<void>;
    logout_error:()=>void;

}