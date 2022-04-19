import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef, ViewChild} from '@angular/core';
import {PerformanceService} from '../../../services/performance.service';
import {HallDto} from '../../../dtos/event/hallDto';
import {SeatSelectDto} from '../../../dtos/seat/seat-select-dto';
import {SeatUnselectDto} from '../../../dtos/seat/seat-unselect-dto';
import {SeatType} from '../../../dtos/seat-type';
import {TicketSeatDto} from '../../../dtos/ticket/ticket-seat-dto';
import {HelperUtils} from '../../../global/helper-utils.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {PerformanceDetailDto} from '../../../dtos/performance/performance-detail-dto';

@Component({
  selector: 'app-hall-view',
  templateUrl: './hall-view.component.html',
  styleUrls: ['./hall-view.component.css']
})

export class HallViewComponent implements OnInit, OnChanges {

  @Input('hall')
  public hall: HallDto = null;

  @Input('performance')
  public performance: PerformanceDetailDto = null;

  @Input('selectedSeats')
  public selectedSeats: TicketSeatDto[] = [];

  @Input('takenSeats')
  public takenSeats: TicketSeatDto[] = [];

  @Output('onSeatSelect')
  public onSeatSelect: EventEmitter<SeatSelectDto> = new EventEmitter<SeatSelectDto>();

  @Output('onSeatUnselect')
  public onSeatUnselect: EventEmitter<SeatUnselectDto> = new EventEmitter<SeatUnselectDto>();

  public location: Location = new Location();
  public screens: Seat[] = [];
  public seatsAndAreas: Seat[] = [];
  public categories: Category[];

  private colors = ['rgba(255, 0, 0, 0.04)', 'rgba(255, 255, 0, 0.04)', 'rgba(100, 149, 237, 0.04)'];
  private ci = 0;

  public errorMessage: String = '';

  public seatNumber: number = 1;

  @ViewChild('chooseSeats')
  private content: TemplateRef<any>;

  constructor(private performanceService: PerformanceService, private helperUtils: HelperUtils, private modalService: NgbModal) {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.hall && changes.hall.previousValue != changes.hall.currentValue) {
      this.renderHall();
      this.updateSeats();
    }
    if ((changes.selectedSeats && changes.selectedSeats.previousValue != changes.selectedSeats.currentValue) || (changes.takenSeats && changes.takenSeats.previousValue != changes.takenSeats.currentValue)) {
      this.updateSeats();
    }
  }

  private renderHall() {
    this.location = new Location(this.hall.name, this.hall.rows, this.hall.cols);

    let categories = this.hall.areas.map(a => a.priceCategory);
    categories = categories.filter((c, i) => c !== null && categories.indexOf(c) === i);
    categories = categories.sort((a, b) => a.price - b.price);

    this.categories = categories.map(c => new Category(c.name, c.price, c.id, this.colors[this.ci++]));

    for (const area of this.hall.areas) {

      if (area.priceCategory !== null) {
        this.categories.forEach(c => {
          if (c.id === area.priceCategory.id) {
            c.areas.push(new CategoryArea(area.startRow, area.startCol, area.endRow + 1, area.endCol + 1));
          }
        });
      }

      switch (area.type) {
        case 'SCREEN':
          this.screens.push(new Seat(area.startCol + '/' + (area.endCol + 1), area.startRow + '/' + (area.endRow + 1), area.name, false, false, SeatType.SCREEN, area.id, new Area(area.startCol, area.endCol, area.startRow, area.endRow)));
          break;
        case 'SEATS':
          for (let i = area.startRow; i <= area.endRow; i++) {
            for (let j = area.startCol; j <= area.endCol; j++) {
              this.seatsAndAreas.push(new Seat(j, i, j, true, false, SeatType.SEATS, area.id, new Area(area.startCol, area.endCol, area.startRow, area.endRow)));
            }
          }
          break;
        case 'SECTION':
          let seatCount = (area.endCol - area.startCol + 1) * (area.endRow - area.startRow + 1)
          this.seatsAndAreas.push(new Seat(area.startCol + '/' + (area.endCol + 1), area.startRow + '/' + (area.endRow + 1), area.name, true, false, SeatType.SECTION, area.id, new Area(area.startCol, area.endCol, area.startRow, area.endRow), false, seatCount));
          break;
      }

    }
  }

  private updateSeats() {
    this.seatsAndAreas.forEach(seat => {
      switch (seat.type) {
        case 'SEATS':
          seat.selected = this.selectedSeats.filter(s => s.col == seat.col && s.row == seat.row).length > 0;
          if (seat.selected) {
            seat.available = true;
          } else {
            seat.available = this.takenSeats.filter(s => s.col == seat.col && s.row == seat.row).length == 0;
          }
          break;
        case 'SECTION':
          let fullSizeCount = (seat.area.endCol - seat.area.startCol + 1) * (seat.area.endRow - seat.area.startRow + 1);
          let takenSizeCount = this.takenSeats.filter(s => s.col >= seat.area.startCol && s.col <= seat.area.endCol && s.row >= seat.area.startRow && s.row <= seat.area.endRow).length;
          seat.selected = this.selectedSeats.filter(s => s.col >= seat.area.startCol && s.col <= seat.area.endCol && s.row >= seat.area.startRow && s.row <= seat.area.endRow).length > 0;
          if (seat.selected) {
            seat.available = true;
          } else {
            seat.available = fullSizeCount > takenSizeCount;
          }
          let seatCount = fullSizeCount - takenSizeCount;
          seat.freeCount = seatCount;
          break;
      }
    });
  }

  /**
   * Send request to server to select or unselect a specific seat or area
   * */
  public toggleSelect(seat: Seat) {
    if (!seat.available) {
      return;
    }

    if (seat.type == SeatType.SECTION && !seat.selected) {
      this.modalService.open(this.content, {}).result.then(() => {
        this.sendSelectRequest(seat);
      });
      return;
    }

    this.sendSelectRequest(seat);
  }

  private sendSelectRequest(seat: Seat) {
    if (seat.selected) {
      const seatUnselectDto = new SeatUnselectDto(null, SeatType[seat.type]);
      if (seat.type == SeatType.SEATS) {
        seatUnselectDto.col = seat.col;
        seatUnselectDto.row = seat.row;
      } else {
        seatUnselectDto.areaId = seat.areaId;
      }
      this.onSeatUnselect.emit(seatUnselectDto);
    } else {
      const seatSelectDto = new SeatSelectDto(null, SeatType[seat.type]);
      if (seat.type == SeatType.SEATS) {
        seatSelectDto.col = seat.col;
        seatSelectDto.row = seat.row;
      } else {
        seatSelectDto.areaId = seat.areaId;
        seatSelectDto.seatCount = this.seatNumber;
      }
      this.onSeatSelect.emit(seatSelectDto);
    }
  }

}

class Seat {
  private selectedColor = 'lightgreen';
  private defaultBorder = '1px solid #9d9d9d';

  constructor(public col, public row, public name, public available, public selected: boolean, public type: SeatType, public areaId: number, public area: Area, public loading: boolean = false, public freeCount: number = 0) {
  }

  getColor() {
    if (this.selected) {
      return this.selectedColor;
    } else if (this.available) {
      return 'white';
    } else {
      return 'grey';
    }
  }

  getBorder() {
    if (this.selected) {
      return 'none';
    } else if (this.available) {
      return this.defaultBorder;
    } else {
      return 'none';
    }
  }
}

class Area {
  constructor(public startCol: number, public endCol: number, public startRow: number, public endRow: number) {
  }
}

class Category {
  public areas: CategoryArea[] = [];

  constructor(public name: string, public price: number, public id: number, public color: string) {
  }
}

class CategoryArea {
  constructor(public rowStart: number, public colStart: number, public rowEnd: number, public colEnd: number) {
  }

  public getCoordinatesAsGridAreaString() {
    return this.rowStart + '/' + this.colStart + '/' + this.rowEnd + '/' + this.colEnd;
  }
}

class Location {
  constructor(public name: string = '', public rows: number = 0, public cols: number = 0) {
  }
}
