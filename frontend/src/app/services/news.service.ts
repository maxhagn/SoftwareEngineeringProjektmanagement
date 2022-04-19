import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {catchError} from 'rxjs/operators';
import {NewsPreviewDto} from '../dtos/news/news-preview-dto';
import {News} from '../dtos/news/news-dto';
import {NewsReadersDto} from "../dtos/news/news-readers-dto";
import {NewsReadDto} from "../dtos/news/news-read-dto";
import {NewsPage} from "../dtos/news/news-page";
import {ArtistPage} from "../dtos/artist/artist-page";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  /**
   * Loads all news from the backend
   */
  getNews(): Observable<NewsPreviewDto[]> {
    return this.httpClient.get<NewsPreviewDto[]>(this.newsBaseUri + '/');
  }

  /**
   * Loads all read news from the backend
   */
  getReadNews(): Observable<NewsPreviewDto[]> {
    return this.httpClient.get<NewsPreviewDto[]>(this.newsBaseUri + '/read');
  }


  /**
   * Loads specific news from the backend
   * @param id of news to load
   */
  getNewsById(id: number): Observable<News> {
    return this.httpClient.get<News>(this.newsBaseUri + '/' + id);
  }


  /**
   * Persists news to the backend
   * @param news to persist
   */
  createNews(news: News): Observable<News> {
    return this.httpClient.post<News>(this.newsBaseUri,
      {
        id: news.id,
        title: news.title,
        text: news.text,
        author: news.author,
        date: news.date,
        summary: news.summary,
        newsCol: news.newsCol,
        event: news.event,
        readers: news.readers,
        newsImages: news.newsImages
      })
      .pipe(
        catchError((err) => {
          /*Handle the error here*/

          return throwError(err);    /*Rethrow it back to component*/
        }));
  }


  /**
   * This method sends a post   request  sind
   * @param newsWithReaders news dto that contains readers
   */
  postReader(newsWithReaders: NewsReadersDto): Observable<NewsReadersDto> {
    const path = this.newsBaseUri + '/' + newsWithReaders.id + '/readers';
    return this.httpClient.put<NewsReadersDto>(path, newsWithReaders)
      .pipe(
        catchError((err) => {
          /*Handle the error here*/

          return throwError(err);    /*Rethrow it back to component*/
        }));
  }

  /**
   * This method lists all unread news
   */
  listUnreadNews() {
    return this.httpClient.get<NewsPreviewDto[]>(this.newsBaseUri + '/list/unread');

  }

  /**
   * This method gets all unread news with pagination
   * @param request for page
   */
  getUnreadNewsPage(request: any):   Observable<NewsPage> {
    const params  = request;
    return this.httpClient.get<NewsPage>(this.newsBaseUri + '/unread', {params});

  }

  /**
   * This method posts a reader for the news entry
   * @param id of news
   */
  postReaderNew(id: number) {
    const path = this.newsBaseUri + '/' + id ;
    return this.httpClient.put<NewsReadersDto>(path, id)
      .pipe(
        catchError((err) => {
          /*Handle the error here*/

          return throwError(err);    /*Rethrow it back to component*/
        }));

  }

  /**
   * This method gets all read pages
   * @param request for pagination
   */
  getReadNewsPage(request: any):  Observable<NewsPage> {
    const params = request;
    return this.httpClient.get<NewsPage>(this.newsBaseUri + '/read', {params});
  }
}
