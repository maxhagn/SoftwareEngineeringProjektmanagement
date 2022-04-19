export class CreateLocation {
  constructor(
    public name: string,
    public street: string,
    public city: string,
    public country: string,
    public area_code: string,
    public categories: CreatePriceCategory[],
    public halls: CreateHall[]
  ) {}
}

export class CreateHall {
  constructor(
    public name: string,
    public cols: number,
    public rows: number,
    public areas: CreateArea[]
  ) {}
}

export class CreateArea {
  constructor(
    public name: string,
    public startCol: number,
    public startRow: number,
    public endCol: number,
    public endRow: number,
    public type: AreaType,
    public tmpPriceCategoryId: number
  ) {}
}

export enum AreaType {
  SCREEN = 'SCREEN',
  AREA = 'SECTION',
  SEATS = 'SEATS'
}


export class CreatePriceCategory {
  constructor(
    public name: string,
    public tmpId: number,
    public price: number
  ) {}
}
