export class TopTenDto {


  constructor( private _event_id: number,
  private _eventTitle: string,
  private _soldTickets: number) {

  }


  get event_id(): number {
    return this._event_id;
  }

  set event_id(value: number) {
    this._event_id = value;
  }

  get eventTitle(): string {
    return this._eventTitle;
  }

  set eventTitle(value: string) {
    this._eventTitle = value;
  }

  get soldTickets(): number {
    return this._soldTickets;
  }

  set soldTickets(value: number) {
    this._soldTickets = value;
  }
}
