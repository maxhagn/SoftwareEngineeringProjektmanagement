import { Component, OnInit } from '@angular/core';
import { TicketSeatDto } from "../../../../dtos/ticket/ticket-seat-dto";
import { HallDto } from "../../../../dtos/event/hallDto";
import { ActivatedRoute } from "@angular/router";
import { PerformanceService } from "../../../../services/performance.service";
import { HelperUtils } from "../../../../global/helper-utils.service";
import { TicketService } from "../../../../services/ticket.service";
import { HallService } from "../../../../services/hall.service";
import { SeatSelectDto } from "../../../../dtos/seat/seat-select-dto";
import { SeatUnselectDto } from "../../../../dtos/seat/seat-unselect-dto";
import { TicketCheckoutDto } from "../../../../dtos/ticket/ticket-checkout-dto";
import {PerformanceDetailDto} from '../../../../dtos/performance/performance-detail-dto';

@Component({
  selector: 'app-tickets-finalize',
  templateUrl: './tickets-finalize.component.html',
  styleUrls: ['./tickets-finalize.component.css']
})
export class TicketsFinalizeComponent implements OnInit {

  public id: number;

  public ticket: TicketCheckoutDto = null;

  public performance: PerformanceDetailDto = null;

  public takenSeats: TicketSeatDto[] = null;

  public selectedSeats: TicketSeatDto[] = null;

  public hall: HallDto = null;

  public errorMessage: string = '';

  constructor(private route: ActivatedRoute, private performanceService: PerformanceService, private helperUtils: HelperUtils, private ticketService: TicketService, private hallService: HallService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
      this.loadTicket(() => {
        this.loadHall();
        this.reloadInfo();
        this.loadPerformance();
      })
    });
  }

  private reloadInfo() {
    this.loadTakenSeats();
    this.loadSelectedSeats();
  }

  private loadHall() {
    this.hallService.getHall(this.ticket.hallId).subscribe(
      (next) => {
        this.hall = next;
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  private loadSelectedSeats() {
    this.ticketService.getSelectedSeats(this.ticket.id).subscribe(
      (next) => {
        this.selectedSeats = next;
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  private loadTicket(success: () => void) {
    this.ticketService.getCheckoutTicketInfo(this.id).subscribe(
      (next) => {
        this.ticket = next;
        success();
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  private loadTakenSeats() {
    this.performanceService.getTakenSeats(this.ticket.performanceId).subscribe(
      (next) => {
        this.takenSeats = next;
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  public selectSeat(seat: SeatSelectDto) {
    seat.ticketId = this.ticket.id;
    this.ticketService.selectSeat(seat).subscribe(
      () => {
        this.reloadInfo();
      },
      (error) => {
        this.loadTakenSeats();
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  public unselectSeat(seat: SeatUnselectDto) {
    seat.ticketId = this.ticket.id;
    this.ticketService.unselectSeat(seat).subscribe(
      () => {
        this.reloadInfo();
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  public getSelectedSeatText(): string[] {
    let outputSeats: string[] = [];
    let groupedSeats: Map<number, TicketSeatDto[]> = new Map<number, TicketSeatDto[]>();
    this.selectedSeats.forEach(
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

  private loadPerformance() {
    this.performanceService.getPerformance(this.ticket.performanceId).subscribe(params => {
      this.performance = params;
    });
  }
}
