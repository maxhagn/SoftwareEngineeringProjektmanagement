import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PerformanceService } from "../../../../services/performance.service";
import { HelperUtils } from "../../../../global/helper-utils.service";
import { PerformanceDetailDto } from "../../../../dtos/performance/performance-detail-dto";
import { TicketService } from "../../../../services/ticket.service";
import { TicketQueueDto } from "../../../../dtos/ticket/ticket-queue-dto";
import { SeatSelectDto } from "../../../../dtos/seat/seat-select-dto";
import { SeatUnselectDto } from "../../../../dtos/seat/seat-unselect-dto";
import { TicketSeatDto } from "../../../../dtos/ticket/ticket-seat-dto";
import { HallDto } from "../../../../dtos/event/hallDto";
import { HallService } from "../../../../services/hall.service";

@Component({
  selector: 'app-tickets-seat-select',
  templateUrl: './tickets-seat-select.component.html',
  styleUrls: ['./tickets-seat-select.component.css']
})
export class TicketsSeatSelectComponent implements OnInit {
  public id: number;

  public performance: PerformanceDetailDto = null;

  public ticket: TicketQueueDto = null;

  public takenSeats: TicketSeatDto[] = null;

  public selectedSeats: TicketSeatDto[] = null;

  public hall: HallDto = null;

  public errorMessage: string = '';

  constructor(private route: ActivatedRoute, private performanceService: PerformanceService, private helperUtils: HelperUtils, private ticketService: TicketService, private hallService: HallService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
      this.loadPerformanceDetails(() => {
        this.loadHall();
        this.loadQueuedTicket(() => {
          this.reloadInfo();
        });
      });
    });
  }

  private reloadInfo() {
    this.loadTakenSeats();
    this.loadSelectedSeats();
  }

  private loadHall() {
    this.hallService.getHall(this.performance.hallId).subscribe(
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

  private loadPerformanceDetails(success: () => void) {
    this.performanceService.getPerformance(this.id).subscribe(
      (next) => {
        this.performance = next;
        success();
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  private loadTakenSeats() {
    this.performanceService.getTakenSeats(this.id).subscribe(
      (next) => {
        this.takenSeats = next;
      },
      (error) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    )
  }

  private loadQueuedTicket(success: () => void) {
    this.ticketService.getOrCreateTicket(this.id).subscribe(
      (next) => {
        this.ticket = next;
        success();
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

  public getSelectedSeatText(seats: TicketSeatDto[]): string[] {
    return this.helperUtils.getSelectedSeatText(seats)
  }

}
