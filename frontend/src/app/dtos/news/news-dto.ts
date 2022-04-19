

class User {
  constructor(
    id: number
  ) {
  }
}

export class News {
  constructor(
    private _id: number,
    private _title: string,
    private _text: string,
    private _author: string,
    private _date: string,
    private _summary: string,
    private _newsCol: string,
    private _event: Event,
    private _readers: User[],
    private _newsImages: NewsImageDto[]) {
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get title(): string {
    return this._title;
  }

  set title(value: string) {
    this._title = value;
  }

  get text(): string {
    return this._text;
  }

  set text(value: string) {
    this._text = value;
  }

  get author(): string {
    return this._author;
  }

  set author(value: string) {
    this._author = value;
  }

  get date(): string {
    return this._date;
  }

  set date(value: string) {
    this._date = value;
  }

  get summary(): string {
    return this._summary;
  }

  set summary(value: string) {
    this._summary = value;
  }

  get newsCol(): string {
    return this._newsCol;
  }

  set newsCol(value: string) {
    this._newsCol = value;
  }

  get event(): Event {
    return this._event;
  }

  set event(value: Event) {
    this._event = value;
  }

  get readers(): User[] {
    return this._readers;
  }

  set readers(value: User[]) {
    this._readers = value;
  }

  get newsImages(): NewsImageDto[] {
    return this._newsImages;
  }

  set newsImages(value: NewsImageDto[]) {
    this._newsImages = value;
  }
}
