export class SignUpUser {
  constructor(private _email: string, private _password: string, private _firstname: string, private _surname: string, private _admin) {
  }

  get email(): string {
    return this._email;
  }

  get password(): string {
    return this._password;
  }

  get firstname(): string {
    return this._firstname;
  }

  get surname(): string {
    return this._surname;
  }

  get admin(): boolean {
    return this._admin;
  }

  set password(value: string) {
    this._password = value;
  }

  set firstname(value: string) {
    this._firstname = value;
  }

  set surname(value: string) {
    this._surname = value;
  }

  set email(value: string) {
    this._email = value;
  }
}
