import { ListUser } from "./list-user";

export class ListUserResult {
  constructor(private _totalPages: number, private _currentPage: number, private _content: ListUser[]) {}


  get totalPages(): number {
    return this._totalPages;
  }

  set totalPages(value: number) {
    this._totalPages = value;
  }

  get currentPage(): number {
    return this._currentPage;
  }

  set currentPage(value: number) {
    this._currentPage = value;
  }

  get content(): ListUser[] {
    return this._content;
  }

  set content(value: ListUser[]) {
    this._content = value;
  }
}
