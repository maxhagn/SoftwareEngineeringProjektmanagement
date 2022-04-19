import {SectionInfo} from '../ticket/SectionInfo';

export class Seat {
  constructor(private _id: number, private _areaId: number, private _row: number, private _col: number[], private _sectionInfo: SectionInfo) {
  }

  get id(): number {
    return this._id;
  }

  get areaId(): number {
    return this._areaId;
  }

  get row(): number {
    return this._row;
  }

  get col(): number[] {
    return this._col;
  }

  get sectionInfo(): SectionInfo {
    return this._sectionInfo;
  }
}
