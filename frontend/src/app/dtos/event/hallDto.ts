export class HallDto {
  constructor(
    private _id: string,
    private _name: string,
    private _rows: number,
    private _cols: number,
    private _areas: any[],
    private _seats: any[]
  ) {}

  get id(): string {
    return this._id;
  }

  set id(value: string) {
    this._id = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get rows(): number {
    return this._rows;
  }

  set rows(value: number) {
    this._rows = value;
  }

  get cols(): number {
    return this._cols;
  }

  set cols(value: number) {
    this._cols = value;
  }

  get areas(): any[] {
    return this._areas;
  }

  set areas(value: any[]) {
    this._areas = value;
  }

  get seats(): any[] {
    return this._seats;
  }

  set seats(value: any[]) {
    this._seats = value;
  }
}
