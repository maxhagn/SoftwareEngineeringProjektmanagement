import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {LocationPersistanceService} from '../../../../services/location-persistance.service';
import {AreaType, CreateArea, CreateHall} from '../../../../dtos/location/create-location';

@Component({
  selector: 'app-hall-create',
  templateUrl: './hall-create.component.html',
  styleUrls: ['./hall-create.component.css']
})
export class HallCreateComponent implements OnInit {
  private items: Item[];

  public rows: number = 10;
  public cols: number = 10;
  public name: string = '';
  public size: number = 2;

  private adding: boolean = false;
  private startPos: GridCor = null;
  private endPos: GridCor = null;

  public areaName: string = '';
  public errors: any = {};

  public deleting = false;
  public selected: Item[] = [];
  public priceCategories: PriceCategory[] = [];
  public selectedCategory: string = 'nothing';
  public selectedType: string = 'nothing';
  public addingScreen: boolean = false;

  constructor(private locationPersistanceService: LocationPersistanceService, private location: Location) {
  }

  ngOnInit(): void {
    const colors = ['green', 'yellow'];
    this.priceCategories = this.locationPersistanceService.loadLocation().categories.map(
      (c, i) => new PriceCategory(c.name, colors[i % colors.length], c.tmpId));
    const halls = this.locationPersistanceService.loadLocation().halls;
    this.name = halls[halls.length - 1].name;
    this.cols = halls[halls.length - 1].cols;
    this.rows = halls[halls.length - 1].rows;
    this.setItemsToDummy();
  }

  setItemsToDummy() {
    this.items = Array(this.cols).fill(0).map(
      (x, i) =>
        Array(this.rows).fill(0).map((y, j) => {
          return new DefaultItem(new GridArea(j + 1, i + 1, j + 1, i + 1));
        })
    ).flat();
  }

  setStartPos(col, row) {
    this.startPos = new GridCor(row, col);
    this.endPos = new GridCor(row, col);
    this.previewArea();
  }

  setEndPos(col, row) {
    if (this.startPos == null) {
      return;
    }

    const curCor = new GridCor(row, col);
    if (!curCor.equals(this.endPos)) {
      this.endPos = curCor;
      this.previewArea();
    }
  }

  previewArea() {
    if (!this.adding) {
      return;
    }
    const {startPos, endPos} = this;
    const area = GridArea.gridAreaFromCors(startPos, endPos);
    let error = false;
    this.items.forEach(item => {
      if (!item.canOverride() && item.isInsideArea(area)) {
        error = true;
      }
    });
    area.isSection(true);
    if (error) {
      console.log('not allowed');
    } else {
      this.items.forEach(item => {
        if (item.isInsideArea(area)) {
          item.tmpColor = 'grey';
        } else {
          item.tmpColor = '';
        }
      });
    }
  }

  addArea() {
    if (!this.adding) {
      return;
    }
    const newItems: Item[] = [];
    const min = new GridCor(this.rows, this.cols);
    const max = new GridCor(0, 0);
    this.items.forEach(item => {
      if (item.tmpColor === '') {
        newItems.push(item);
      } else {
        // get min and max pos
        if (item.area.colStart < min.col) {
          min.col = item.area.colStart;
        }
        if (item.area.rowStart < min.row) {
          min.row = item.area.rowStart;
        }
        if (item.area.colStart > max.col) {
          max.col = item.area.colStart;
        }
        if (item.area.rowStart > max.row) {
          max.row = item.area.rowStart;
        }
      }
    });

    const priceCategory = this.priceCategories.filter(p => p.name === this.selectedCategory)[0];
    const area = GridArea.gridAreaFromCors(min, max);

    if (this.selectedType === 'Section') {
      area.isSection(true);
      newItems.push(new Sector(area, this.areaName, priceCategory.color, priceCategory.tmpId));
    } else if (this.selectedType === 'Seats') {
      for (let i = min.col; i <= max.col; i++) {
        for (let j = min.row; j <= max.row; j++) {
          newItems.push(new Seat(new GridArea(j, i, j, i), priceCategory.color));
        }
      }
      area.isSection(true);
      newItems.push(new SeatSection(area, this.areaName, priceCategory.tmpId));
    } else if (this.addingScreen) {
      this.addingScreen = false;
      area.isSection(true);
      newItems.push(new Screen(area));
    }

    this.resetForm();
    this.items = newItems;
  }

  addSector() {
    this.errors = {};

    if (this.areaName === '') {
      this.errors.name = true;
    }
    if (this.selectedCategory === 'nothing') {
      this.errors.category = true;
    }
    if (this.selectedType === 'nothing') {
      this.errors.type = true;
    }

    if (Object.keys(this.errors).length !== 0) {
      return;
    }
    this.adding = true;
    this.startPos = null;
    this.endPos = null;
  }

  addScreen() {
    this.resetForm();
    this.adding = true;
    this.addingScreen = true;
  }

  resetForm() {
    this.areaName = '';
    this.selectedCategory = 'nothing';
    this.selectedType = 'nothing';
    this.adding = false;
    this.startPos = null;
    this.endPos = null;
    this.addingScreen = false;
    this.items.forEach(item => {
      item.tmpColor = '';
    });
  }

  returnFalse() {
    return false;
  }

  editItem(item: Item) {
    if (this.deleting) {
      this.selected.push(item);
      item.toggleSelect();
    }
  }

  startDeleting() {
    this.deleting = true;
  }

  deleteSelection() {
    this.selected.forEach((item => {
      this.items = this.items.filter(i => !i.equals(item));
      if (item.isSeatSection()) {
        this.items = this.items.filter(i => !i.area.intersects(item.area));
      }
      for (let i = item.area.rowStart; i <= item.area.rowEnd; i++) {
        for (let j = item.area.colStart; j <= item.area.colEnd; j++) {
          this.items.push(new DefaultItem(new GridArea(i, j, i, j)));
        }
      }
    }));
    this.deleting = false;
  }

  cancelSelection() {
    this.selected.forEach(item => {
      item.toggleSelect();
    });
    this.selected = [];
    this.deleting = false;
  }


  save() {
    let screen = false;
    let area = false;
    const areas = [];
    this.items.forEach( item => {
      if (item.isScreen()) {
        areas.push(new CreateArea(
          'Screen', item.area.colStart, item.area.rowStart, item.area.colEnd, item.area.rowEnd, AreaType.SCREEN, 0));
        screen = true;
      }
      if (item.isSeatSection() || item.isSector()) {
        const typ = item.isSeatSection() ? AreaType.SEATS : AreaType.AREA;
        areas.push(new CreateArea(
          // @ts-ignore
          item.name, item.area.colStart, item.area.rowStart, item.area.colEnd, item.area.rowEnd, typ, item.categoryTmpId));
        area = true;
      }
    });
    this.errors = [];
    if (!screen) {
      this.errors['noscreen'] = true;
    }
    if (!area) {
      this.errors['nosection'] = true;
    }

    if (!screen || !area) {
      return;
    }

    const hall = new CreateHall(this.name, this.cols, this.rows, areas);
    this.locationPersistanceService.replaceLastHall(hall);
    this.location.back();
  }

  abort() {
    const halls = this.locationPersistanceService.loadLocation().halls;
    halls.splice(halls.length - 1, 1);
    this.location.back();
  }

  addSection() {
    this.selectedType = 'Section';
    this.addSector();
  }

  addSeats() {
    this.selectedType = 'Seats';
    this.addSector();
  }

  selectType() {
    this.areaName = this.selectedCategory;
  }
}

class GridCor {
  constructor(public row: number,
              public col: number
  ) {}

  equals(cor: GridCor) {
    return this.row === cor.row && this.col === cor.col;
  }
}
class GridArea {
  public section: boolean;
  constructor(public rowStart: number,
              public colStart: number,
              public rowEnd: number,
              public colEnd: number
  ) {}

  static gridAreaFromCors(a: GridCor, b: GridCor): GridArea {
    const rowStart = Math.min(a.row, b.row);
    const rowEnd = Math.max(a.row, b.row);
    const colStart = Math.min(a.col, b.col);
    const colEnd = Math.max(a.col, b.col);

    return new GridArea(rowStart, colStart, rowEnd, colEnd);
  }

  toString() {
    return this.rowStart + '/' +
      this.colStart + '/' +
      (this.rowEnd + (this.section ? 1 : 0)) +
      '/' + (this.colEnd + (this.section ? 1 : 0));
  }

  intersects(area: GridArea): boolean {
    let colIntersect = false;
    let rowIntersect = false;

    if (this.rowStart >= area.rowStart && this.rowStart <= area.rowEnd ||
        this.rowEnd >= area.rowStart && this.rowEnd <= area.rowEnd) { rowIntersect = true; }

    if (area.rowStart >= this.rowStart && area.rowStart <= this.rowEnd ||
      area.rowEnd >= this.rowStart && area.rowEnd <= this.rowEnd) { rowIntersect = true; }

    if (this.colStart >= area.colEnd && this.colStart <= area.colEnd ||
        this.colEnd >= area.colStart && this.colEnd <= area.colEnd) { colIntersect = true; }

    if (area.colStart >= this.colEnd && area.colStart <= this.colEnd ||
      area.colEnd >= this.colStart && area.colEnd <= this.colEnd) { colIntersect = true; }

    return colIntersect && rowIntersect;
  }

  isSection(section: boolean) {
    this.section = section;
  }

  equals(area: GridArea): boolean {
    return this.rowStart === area.rowStart && this.rowEnd === area.rowEnd && this.colStart === area.colStart && this.colEnd === area.colEnd;
  }
}

abstract class Item {
  area: GridArea;
  color: string;
  border: string = '1px solid lightgrey';
  tmpColor: string;
  name: string;

  private selected = false;

  toggleSelect() {
    if (this.selected) {
      this.selected = false;
      this.border = '1px solid lightgrey';
    } else {
      this.selected = true;
      this.border = '2px dashed black';
    }
  }

  isDefaultItem() {
    return this instanceof DefaultItem;
  }

  isSector() {
    return this instanceof Sector;
  }

  isSeat() {
    return this instanceof Seat;
  }

  isSeatSection() {
    return this instanceof SeatSection;

  }

  isInsideArea(area: GridArea): boolean {
    return this.area.intersects(area);
  }

  equals(item: Item): boolean {
    return this.area.equals(item.area);
  }

  abstract canOverride(): boolean;

  isScreen() {
    return this instanceof Screen;
  }
}

class DefaultItem extends Item {
  color = 'white';

  constructor(public area: GridArea) {
    super();
  }

  canOverride(): boolean {
    return true;
  }
}

class Sector extends Item {
  constructor(public area: GridArea,
              public name: string,
              public color: string,
              public categoryTmpId: number) {
    super();
  }

  canOverride(): boolean {
    return false;
  }
}
class Screen extends Item {
  constructor(public area: GridArea) {
    super();
  }

  canOverride(): boolean {
    return false;
  }
}

class SeatSection extends Item {
  constructor(public area: GridArea,
              public name: string,
              public categoryTmpId: number) {
    super();
  }

  canOverride(): boolean {
    return false;
  }
}

class Seat extends Item {
  constructor(public area: GridArea,
              public color: string) {
    super();
  }

  canOverride(): boolean {
    return false;
  }
}

class PriceCategory {
  constructor(public name: string,
              public color: string,
              public tmpId: number) {}
}
