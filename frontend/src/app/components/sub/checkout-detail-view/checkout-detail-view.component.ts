import { Component, Input, OnInit } from '@angular/core';
import { TicketCheckoutDto } from "../../../dtos/ticket/ticket-checkout-dto";
import { TicketSeatDto } from "../../../dtos/ticket/ticket-seat-dto";
import { HelperUtils } from "../../../global/helper-utils.service";

@Component({
  selector: 'app-checkout-detail-view',
  templateUrl: './checkout-detail-view.component.html',
  styleUrls: ['./checkout-detail-view.component.css']
})
export class CheckoutDetailViewComponent implements OnInit {

  @Input('ticket')
  public ticket: TicketCheckoutDto;

  constructor(private helperUtils: HelperUtils) { }

  ngOnInit(): void {
  }

  public getTotal(): number {
    if(this.ticket.seats.length == 0) {
      return 0;
    }
    return this.ticket.seats.map(v => v.price).reduce(
      (pre, cur) => {
        return pre + cur;
      }
    )
  }

  public getSelectedSeatText(seats: TicketSeatDto[]): string[] {
    return this.helperUtils.getSelectedSeatText(seats)
  }

}
