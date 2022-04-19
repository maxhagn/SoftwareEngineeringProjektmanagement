import {EventDto} from '../event-dto';
import {Seat} from '../seat/seat';

export class TicketPreview {
  constructor(private _id: number, private _event: EventDto, private _date: Date, private _seats: Seat[], private _barcode: string, private _hall: string, private _status: string) {
  }

  get status(): string {
    return this._status;
  }

  set status(value: string) {
    this._status = value;
  }

  get id(): number {
    return this._id;
  }

  get event(): EventDto {
    return this._event;
  }

  get date(): Date {
    return this._date;
  }

  get seats(): Seat[] {
    return this._seats;
  }

  get barcode(): string {
    return this._barcode;
  }

  get hall(): string {
    return this._hall;
  }
}
