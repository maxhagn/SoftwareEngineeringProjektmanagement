export class PerformanceDetailDto {

  // tslint:disable-next-line:max-line-length
  constructor(private _id: number, private _hallId: number, private _title: string, private _locationName: string, private _hallName: string, private _date: Date, private _duration: Date, public minPrice: number) {
  }

  get hallId(): number {
    return this._hallId;
  }

  set hallId(value: number) {
    this._hallId = value;
  }

  get id(): number {
    return this._id;
  }

  get title(): string {
    return this._title;
  }

  get locationName(): string {
    return this._locationName;
  }

  get hallName(): string {
    return this._hallName;
  }

  get date(): Date {
    return this._date;
  }

  get duration(): Date {
    return this._duration;
  }

  set id(value: number) {
    this._id = value;
  }

  set title(value: string) {
    this._title = value;
  }

  set locationName(value: string) {
    this._locationName = value;
  }

  set hallName(value: string) {
    this._hallName = value;
  }

  set date(value: Date) {
    this._date = value;
  }

  set duration(value: Date) {
    this._duration = value;
  }
}
