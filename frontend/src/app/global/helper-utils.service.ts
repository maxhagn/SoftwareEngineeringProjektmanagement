import { Injectable } from '@angular/core';
import { TicketSeatDto } from "../dtos/ticket/ticket-seat-dto";
import {Seat} from '../dtos/seat/seat';

@Injectable({
  providedIn: 'root'
})
export class HelperUtils {
  public getErrorText(error): string {
    if (typeof error.error === 'object') {
      if (error.error.error) {
        return error.error.error;
      }
      if (error.message) {
        return error.message;
      }
    } else {
      return error.error;
    }
  }

  public getSelectedSeatText(seats: TicketSeatDto[]): string[] {
    let outputSeats: string[] = [];
    let groupedSeats: Map<number, TicketSeatDto[]> = new Map<number, TicketSeatDto[]>();
    seats.forEach(
      (seat) => {
        if (seat.isSection) {
          if (groupedSeats.has(seat.areaName)) {
            groupedSeats.get(seat.areaName).push(seat);
          } else {
            groupedSeats.set(seat.areaName, [seat]);
          }
        } else {
          outputSeats.push(`Sector ${ seat.areaName } | Row ${ seat.row } | Seat ${ seat.col } | ${ seat.price } €`);
        }
      }
    );
    groupedSeats.forEach(
      (value, key) => {
        outputSeats.push(`Sector ${ key } | Count ${ value.length } | ${ value[0].price * value.length } €`);
      }
    );
    return outputSeats;
  }

}
