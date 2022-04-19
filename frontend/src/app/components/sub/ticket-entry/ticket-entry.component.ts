import {Component, Input, OnInit} from '@angular/core';
import {Location} from '../../../dtos/location/location';
import {Ticket} from '../../../dtos/ticket/ticket';
import {Seat} from '../../../dtos/ticket/seat';

@Component({
  selector: 'app-ticket-entry',
  templateUrl: './ticket-entry.component.html',
  styleUrls: ['./ticket-entry.component.css']
})
export class TicketEntryComponent implements OnInit {

  @Input() entry: Ticket;
  public seatsTypeSeats: Seat[] = new Array<Seat>();
  public seatsTypeOther: Map<String, Seat[]> = new Map<String, Seat[]>();

  constructor() {
  }

  ngOnInit(): void {
    this.entry.seats.forEach(
      (seat) => {
        if (seat.area.type === 'SEATS') {
          this.seatsTypeSeats.push(seat);
        } else {
          if (this.seatsTypeOther.get(seat.area.name) == null) {
            const tmpSeats: Seat[] = new Array<Seat>();
            tmpSeats.push(seat);
            this.seatsTypeOther.set(seat.area.name, tmpSeats);
          } else {
            const addedSeats: Seat[] = new Array<Seat>();
            this.seatsTypeOther.get(seat.area.name).forEach(
              (s: Seat) => {
                addedSeats.push(s);
              }
            );
            addedSeats.push(seat);
            this.seatsTypeOther.set(seat.area.name, addedSeats);
          }
        }
      }
    );
  }

  public getKeys(): String[] {
    return Array.from(this.seatsTypeOther.keys());
  }

  public getValues(key: String): Seat[] {
    return Array.from(this.seatsTypeOther.get(key));
  }

  public getPrice(key: String): number {
    return this.seatsTypeOther.get(key)[0].area.priceCategory.price;
  }

  /*public concatSeatString(seats: Seat[]) {
    let out: String = '';
    seats.forEach(
      (seat: Seat) => {
        out += 'Row ' + seat.seatRow + ' Col ' + seat.seatCol + ', ';
      }
    );
    return out.substr(0, out.length - 2);
  }*/
}
