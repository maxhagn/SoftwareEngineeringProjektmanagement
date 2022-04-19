import { TicketSeatDto } from "./ticket-seat-dto";

export class TicketQueueDto {

  constructor(private _id: number, private _seats: TicketSeatDto[]) {
  }

  get id(): number {
    return this._id;
  }

  get seats(): TicketSeatDto[] {
    return this._seats;
  }
}
