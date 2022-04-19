import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import * as jwt_decode from 'jwt-decode';
import { Globals } from '../global/globals';
import { Role } from '../global/role';
import { SignInUser } from '../dtos/user/sign-in-user';
import { SignUpUser } from '../dtos/user/sign-up-user';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  /*Curr user*/
  currentUser: SignUpUser;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  amAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN;
  }

  signUp(signUpUser: SignUpUser): Observable<any> {
    console.log('signup in auth.service with uri ' + this.globals.signupUri + ' and SignUpUser...');
    console.log(signUpUser);
    return this.httpClient
      .post(this.globals.signupUri,
        {
          email: signUpUser.email,
          password: signUpUser.password,
          firstname: signUpUser.firstname,
          surname: signUpUser.surname
        });
  }


  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   * @param signInUser User data to sign in
   */
  signIn(signInUser: SignInUser): Observable<string> {
    return this.httpClient
      .post(this.globals.signinUri, { email: signInUser.email, password: signInUser.password }, { responseType: 'text' })
      .pipe(tap((authResponse: string) => this.setToken(authResponse)));
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isSignedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  /**
   * Deletes the session of the current user
   * */
  logoutUser() {
    localStorage.removeItem('authToken');
  }

  /**
   * Returns the token of the current signed in user
   * */
  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole(): Role {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return Role.ADMIN;
      } else if (authInfo.includes('ROLE_USER')) {
        return Role.USER;
      }
    }
    return Role.UNDEFINED;
  }


  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }


  create(signUpUser: SignUpUser): Observable<string> {
    console.log('signup in auth.service with uri ' + this.globals.userCreateUri + ' and SignUpUser...');
    console.log(signUpUser);
    return this.httpClient
      .post(this.globals.userCreateUri,
        {
          email: signUpUser.email,
          password: signUpUser.password,
          firstname: signUpUser.firstname,
          surname: signUpUser.surname,
          admin: signUpUser.admin
        },
        {
          responseType: 'text',
          headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*' })
        });
  }
}
