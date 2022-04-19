import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/pages/home/home.component';
import {LoginComponent} from './components/pages/auth/sign-in/login.component';
import {AuthGuard} from './guards/auth.guard';
import {SignUpComponent} from './components/pages/auth/sign-up/sign-up.component';
import {NewsViewComponent} from './components/pages/news/news-view/news-view.component';
import {NewsDetailComponent} from './components/pages/news/news-detail/news-detail.component';
import {NewsCreateComponent} from './components/pages/news/news-create/news-create.component';
import {EventsViewComponent} from './components/pages/events/events-view/events-view.component';
import {EventsDetailComponent} from './components/pages/events/events-detail/events-detail.component';
import {TicketsSeatSelectComponent} from './components/pages/tickets/tickets-seat-select/tickets-seat-select.component';
import {CheckoutBuyComponent} from './components/pages/checkout/checkout-buy/checkout-buy.component';
import {CheckoutReserveComponent} from './components/pages/checkout/checkout-reserve/checkout-reserve.component';
import {LocationsViewComponent} from './components/pages/locations/locations-view/locations-view.component';
import {LocationsDetailComponent} from './components/pages/locations/locations-detail/locations-detail.component';
import {UsersViewComponent} from './components/pages/users/users-view/users-view.component';
import {UsersDetailComponent} from './components/pages/users/users-detail/users-detail.component';
import {UsersCreateComponent} from './components/pages/users/users-create/users-create.component';
import {EventsCreateComponent} from './components/pages/events/events-create/events-create.component';
import {TicketsViewComponent} from './components/pages/tickets/tickets-view/tickets-view.component';
import {TicketsDetailComponent} from './components/pages/tickets/tickets-detail/tickets-detail.component';
import {ReversalInvoivePrintComponent} from './components/pages/reversal-invoive-print/reversal-invoive-print.component';
import {BillsPrintComponent} from './components/pages/bills/bills-print/bills-print.component';
import {LocationsCreateComponent} from './components/pages/locations/locations-create/locations-create.component';
import {ArtistsViewComponent} from './components/pages/artists/artists-view/artists-view.component';
import {ArtistsDetailComponent} from './components/pages/artists/artists-detail/artists-detail.component';
import {EventsTopComponent} from './components/pages/events/events-top/events-top.component';
import {HallViewComponent} from './components/sub/hall-view/hall-view.component';
import {HallCreateComponent} from './components/pages/hall/hall-create/hall-create.component';
import {TicketsPrintComponent} from './components/pages/tickets/tickets-print/tickets-print.component';
import {PerformancesPageComponent} from './components/pages/performances/performances-page/performances-page.component';
import {PerformancesViewComponent} from './components/pages/performances/performances-view/performances-view.component';
import { TicketsFinalizeComponent } from './components/pages/tickets/tickets-finalize/tickets-finalize.component';
import {ResetPwComponent} from './components/pages/auth/reset-pw/reset-pw.component';

const routes: Routes = [

  /* -- PUBLIC -- */
  {path: '', component: HomeComponent},
  {path: 'resetPw/:token', component: ResetPwComponent},
  {path: 'login', component: LoginComponent},
  {path: 'sign_up', component: SignUpComponent},

  /* -- PRIVATE -- */

  {path: 'home', canActivate: [AuthGuard], component: NewsViewComponent},
  {path: 'news/create', canActivate: [AuthGuard], component: NewsCreateComponent},
  {path: 'news/:id', canActivate: [AuthGuard], component: NewsDetailComponent},

  {path: 'events', canActivate: [AuthGuard], component: EventsViewComponent},
  {path: 'events/top', canActivate: [AuthGuard], component: EventsTopComponent},
  {path: 'event/create', canActivate: [AuthGuard], component: EventsCreateComponent},
  {path: 'event/:id', canActivate: [AuthGuard], component: EventsDetailComponent},

  {path: 'select_seats/:id', canActivate: [AuthGuard], component: TicketsSeatSelectComponent},

  {path: 'buy/:id', canActivate: [AuthGuard], component: CheckoutBuyComponent},
  {path: 'reserve/:id', canActivate: [AuthGuard], component: CheckoutReserveComponent},

  {path: 'performances', canActivate: [AuthGuard], component: PerformancesPageComponent},
  {path: 'performances/:id', canActivate: [AuthGuard], component: PerformancesViewComponent},

  {path: 'locations', canActivate: [AuthGuard], component: LocationsViewComponent},
  {path: 'location/create', canActivate: [AuthGuard], component: LocationsCreateComponent},
  {path: 'location/:id', canActivate: [AuthGuard], component: LocationsDetailComponent},

  {path: 'users', canActivate: [AuthGuard], component: UsersViewComponent},
  {path: 'user/create', canActivate: [AuthGuard], component: UsersCreateComponent},

  {path: 'tickets', canActivate: [AuthGuard], component: TicketsViewComponent},
  {path: 'ticket/:id/print', canActivate: [AuthGuard], component: TicketsPrintComponent},
  {path: 'ticket/:id', canActivate: [AuthGuard], component: TicketsDetailComponent},
  {path: 'ticket/finalize/:id', canActivate: [AuthGuard], component: TicketsFinalizeComponent},

  {path: 'reversal_invoice', canActivate: [AuthGuard], component: ReversalInvoivePrintComponent},
  {path: 'invoice', canActivate: [AuthGuard], component: BillsPrintComponent},

  {path: 'artists', canActivate: [AuthGuard], component: ArtistsViewComponent},
  {path: 'artist/:id', canActivate: [AuthGuard], component: ArtistsDetailComponent},

  {path: 'hall/create', canActivate: [AuthGuard], component: HallCreateComponent},
  {path: 'hall/:id', canActivate: [AuthGuard], component: HallViewComponent},

  {path: 'profile', canActivate: [AuthGuard], component: UsersDetailComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
