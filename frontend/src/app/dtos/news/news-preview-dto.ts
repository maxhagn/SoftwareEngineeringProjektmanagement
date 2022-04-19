class User {
  constructor(
    id: number
  ) {
  }
}

class NewsImages {
  constructor(
    id: number
  ) {
  }
}

export class NewsPreviewDto {
  constructor(
    private _id: number,
    private _title: string,
    private _text: string,
    private _author: string,
    private _date: string,
    private _summary: string,
    private _newsImages: NewsImages[]) {}

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

  get newsImages(): NewsImages[] {
    return this._newsImages;
  }

  set newsImages(value: NewsImages[]) {
    this._newsImages = value;
  }
}
