import { SeatType } from "../seat-type";

export class SeatSelectDto {

  private _row: number;
  private _col: number;
  private _areaId: number;
  private _seatCount: number;

  constructor(private _ticketId: number, private _type: SeatType) {}

  set row(value: number) {
    this._row = value;
  }

  set col(value: number) {
    this._col = value;
  }

  set areaId(value: number) {
    this._areaId = value;
  }

  set seatCount(value: number) {
    this._seatCount = value;
  }

  set ticketId(value: number) {
    this._ticketId = value;
  }

  set type(value: SeatType) {
    this._type = value;
  }

  get row(): number {
    return this._row;
  }

  get col(): number {
    return this._col;
  }

  get areaId(): number {
    return this._areaId;
  }

  get seatCount(): number {
    return this._seatCount;
  }

  get ticketId(): number {
    return this._ticketId;
  }

  get type(): SeatType {
    return this._type;
  }
}
