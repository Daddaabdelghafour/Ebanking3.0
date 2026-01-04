export interface User {
  id: number;
  keycloakUserId: string;
  email: string;
  firstName: string;
  lastName:  string;
  phoneNumber?:  string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER' | 'PREFER_NOT_TO_SAY';
  address?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  profilePictureUrl?: string;
  bio?: string;
  emailNotifications:  boolean;
  smsNotifications: boolean;
  preferredLanguage:  string;
  timezone: string;
  active: boolean;
  emailVerified: boolean;
  phoneVerified: boolean;
  lastLoginAt?:  string;
  loginCount:  number;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  bio?: string;
  emailNotifications?: boolean;
  smsNotifications?:  boolean;
  preferredLanguage?: string;
  timezone?: string;
}
