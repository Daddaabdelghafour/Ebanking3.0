import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, UpdateProfileRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  /**
   * Récupérer le profil
   */
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile`);
  }

  /**
   * Mettre à jour le profil
   */
  updateProfile(request: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/profile`, request);
  }

  /**
   * Supprimer le compte
   */
  deleteAccount(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/profile`);
  }
}
