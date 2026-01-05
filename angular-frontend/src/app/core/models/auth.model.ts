export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  // Champs obligatoires
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  cinOrPassport: string;
  nationality: string;
  gender: string;
  phone: string;
  dateOfBirth?: string;
  
  // Champs optionnels
  address?: string;
  city?: string;
  country?: string;
  profession?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  keycloakUserId: string;
  email: string;
  roles: string[];
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data?: any;
}

export interface VerifyEmailRequest {
  email: string;
  verificationCode: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  code: string;
  newPassword:  string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword:  string;
}


// Enum des rôles utilisateur
export enum UserRole {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER',
  AGENT_BANK = 'AGENT-BANK'
}