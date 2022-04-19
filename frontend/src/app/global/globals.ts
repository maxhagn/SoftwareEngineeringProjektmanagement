import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  readonly backendUri: string = 'http://localhost:8080/api/v1';
  readonly signinUri: string = this.backendUri + '/signin';
  readonly signupUri: string = this.backendUri + '/signup';
  readonly userCreateUri: string = this.backendUri + '/user';
  readonly listUserUri: string = this.backendUri + '/user';
  readonly performanceUri: string = this.backendUri + '/performance';
  readonly ticketUri: string = this.backendUri + '/ticket';
  readonly hallUri: string = this.backendUri + '/hall';
}
