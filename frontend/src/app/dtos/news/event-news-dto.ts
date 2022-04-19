import {News} from './news-dto';


class Artist {
}

class EventImages {
}

export class EventDto {
  constructor(
    public id: number,
    public title: string,
    public description: string,
    public duration: number,
    public artist: Artist,
    public news: News[],
    public performances: Performance[],
    public eventImages: EventImages[]
  ) {
  }
}
