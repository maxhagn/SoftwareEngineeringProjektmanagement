export class TicketSeatDto {

  constructor(private _areaName: number, private _isSection: Boolean, private _row: number, private _col: number, private _price: number) {
  }

  get isSection(): Boolean {
    return this._isSection;
  }

  get areaName(): number {
    return this._areaName;
  }

  get row(): number {
    return this._row;
  }

  get col(): number {
    return this._col;
  }

  get price(): number {
    return this._price;
  }
}
