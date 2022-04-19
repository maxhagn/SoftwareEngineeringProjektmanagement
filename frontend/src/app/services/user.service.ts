import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import * as jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {Role} from '../global/role';
import {SignInUser} from '../dtos/user/sign-in-user';
import {SignUpUser} from '../dtos/user/sign-up-user';
import {ListUserResult} from '../dtos/user/list-user-result';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * @param page which page to load
   * @return All users on the specified page
   * */
  public listUsers(page: number): Observable<ListUserResult> {
    let params = new HttpParams();
    params = params.append('page', page.toString());
    return this.httpClient.get<ListUserResult>(this.globals.listUserUri, {
      params: params
    });
  }

  /**
   * @param id of the user to lock
   * @return Observable that states if request was successful
   * */
  public lockUser(id: number): Observable<void> {
    return this.httpClient.post<any>(this.globals.backendUri + `/user/${id}/lock`, {});
  }

  /**
   * @param id of the user to reset password
   * @return Observable that states if request was successful
   * */
  public resetPassword(id: number): Observable<void> {
    return this.httpClient.post<any>(this.globals.backendUri + `/user/${id}/password`, {}, {responseType: 'json'});
  }

  /**
   * @param token resetToken
   * @param pw new pw
   * @return Observable that states if request was successful
   * */
  public actuallyResetPw(token: string, pw: string): Observable<void> {
    return this.httpClient.post<any>(this.globals.backendUri + `/user/resetPw`, {token, pw});
  }

  /**
   * @param id of the user to unlock
   * @return Observable that states if request was successful
   * */
  public unlockUser(id: number): Observable<void> {
    return this.httpClient.post<any>(this.globals.backendUri + `/user/${id}/unlock`, {});
  }

  updateUser(updateUser: SignUpUser): Observable<object> {
    return this.httpClient
      .put(this.globals.backendUri + '/user/',
        {
          email: updateUser.email,
          password: updateUser.password,
          firstname: updateUser.firstname,
          surname: updateUser.surname
        },
        {
          responseType: 'json',
          headers: new HttpHeaders({'Access-Control-Allow-Origin': '*'})
        });
  }

  getUser() {
    return this.httpClient.get<SignUpUser>(this.globals.backendUri + '/user/0', {});
  }

  delete() {
    return this.httpClient.delete(this.globals.backendUri + '/user', {});
  }
}
