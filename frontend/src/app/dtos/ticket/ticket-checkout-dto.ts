import { TicketSeatDto } from "./ticket-seat-dto";

export class TicketCheckoutDto {

  constructor(private _id: number, private _performanceId: number, private _hallId: number, private _title: string, private _locationName: string, private _hallName: string, private _date: Date, private _seats: TicketSeatDto[]) {
  }

  get hallId(): number {
    return this._hallId;
  }

  set hallId(value: number) {
    this._hallId = value;
  }

  get performanceId(): number {
    return this._performanceId;
  }

  set performanceId(value: number) {
    this._performanceId = value;
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

  get seats(): TicketSeatDto[] {
    return this._seats;
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

  set seats(value: TicketSeatDto[]) {
    this._seats = value;
  }
}
