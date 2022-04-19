import { Component, OnInit } from '@angular/core';
import { TicketCheckoutDto } from "../../../../dtos/ticket/ticket-checkout-dto";
import { ActivatedRoute, Router } from "@angular/router";
import { TicketService } from "../../../../services/ticket.service";
import { HelperUtils } from "../../../../global/helper-utils.service";
import { MessageService } from "../../../../services/message.service";

@Component({
  selector: 'app-checkout-reserve',
  templateUrl: './checkout-reserve.component.html',
  styleUrls: ['./checkout-reserve.component.css']
})
export class CheckoutReserveComponent implements OnInit {

  public id: number;
  public ticket: TicketCheckoutDto;

  public errorMessage: string = '';

  constructor(private route: ActivatedRoute, private router: Router, private ticketService: TicketService, private helperUtils: HelperUtils, private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
      this.loadTicketInfo();
    });
  }

  private loadTicketInfo() {
    this.ticketService.getCheckoutTicketInfo(this.id).subscribe(
      (next) => {
        this.ticket = next;
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error)
      }
    )
  }

  public reserveTicket() {
    this.ticketService.reserveTicket(this.id).subscribe(
      () => {
        this.messageService.publishMessage("Successfuly reserved ticket");
        this.router.navigate(['/ticket', this.id]);
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      });
  }

}
