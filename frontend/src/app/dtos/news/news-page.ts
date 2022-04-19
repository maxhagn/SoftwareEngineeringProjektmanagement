import {News} from "./news-dto";
import {NewsPreviewDto} from "./news-preview-dto";

export class NewsPage {
  constructor(
    public size: number,
    public first: boolean,
    public last: boolean,
    public content: NewsPreviewDto[],
    public totalPages: number,
    public totalElements: number
  ) {
  }


}
