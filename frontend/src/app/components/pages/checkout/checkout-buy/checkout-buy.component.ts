import { Component, OnInit } from '@angular/core';
import { TicketService } from "../../../../services/ticket.service";
import { ActivatedRoute, Router } from "@angular/router";
import { TicketCheckoutDto } from "../../../../dtos/ticket/ticket-checkout-dto";
import { HelperUtils } from "../../../../global/helper-utils.service";
import { TicketSeatDto } from "../../../../dtos/ticket/ticket-seat-dto";
import { MessageService } from "../../../../services/message.service";

@Component({
  selector: 'app-checkout-buy',
  templateUrl: './checkout-buy.component.html',
  styleUrls: ['./checkout-buy.component.css']
})
export class CheckoutBuyComponent implements OnInit {

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

  public buyTicket() {
    this.ticketService.buyTicket(this.id).subscribe(
      () => {
        this.messageService.publishMessage("Successfuly bought ticket");
        this.router.navigate(['/ticket', this.id]);
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      });
  }

}
