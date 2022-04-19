package at.ac.tuwien.sepm.groupphase.backend.utils;

import at.ac.tuwien.sepm.groupphase.backend.entity.Area;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.html2pdf.HtmlConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class InvoicePdfCreator {

    private static DecimalFormat df = new DecimalFormat("0.00");

    public static void createPdf(String outputPath, Ticket ticket) throws Exception {
       DateTimeFormatter format = DateTimeFormatter.ofPattern("dd. M. yy");
       String now = LocalDate.now().format(format);

       LocalDate performanceLocalDate = ticket.getPerformance().getDatetime().toLocalDate();
       String performanceDate = performanceLocalDate.format(format);

       List<Seat> seats = ticket.getSeats();

       String replacedHtml = html;
       replacedHtml = replacedHtml.replace("%nowDate%", now);
       replacedHtml = replacedHtml.replace("%performanceDate%", performanceDate);
       replacedHtml = replacedHtml.replace("%invoiceNr%", ticket.getId().toString());

       AtomicReference<String> replacedRows = new AtomicReference<>("");

       Map<Area, Integer> items = new HashMap<>();

       for (Seat s : seats) {
           int newCount = items.getOrDefault(s.getArea(), 0) + 1;
           items.put(s.getArea(), newCount);
       }

       AtomicReference<Double> sumBrutto = new AtomicReference<>((double)0);
       AtomicReference<Double> sumNetto = new AtomicReference<>((double) 0);

       items.forEach((area, count) -> {
           String oneRow = row;
           oneRow = oneRow.replace("%description%", area.getName());
           oneRow = oneRow.replace("%amount%", count.toString());
           float p = area.getPriceCategory().getPrice();
           double netto = p/1.2;
           oneRow = oneRow.replace("%price%", ""+df.format(p));
           oneRow = oneRow.replace("%netto%", ""+ df.format(netto));

           sumBrutto.updateAndGet(v -> (v + count * p));
           sumNetto.updateAndGet(v -> (v + netto * count));

           replacedRows.set(replacedRows.get() + oneRow);
       });

       double netto = sumNetto.get();
       double brutto = sumBrutto.get();
       double ust = brutto - netto;

       replacedHtml = replacedHtml.replace("%rows%", replacedRows.get());
       replacedHtml = replacedHtml.replace("%nettoSumme%", ""+df.format(netto));
       replacedHtml = replacedHtml.replace("%ust%", ""+df.format(ust));
       replacedHtml = replacedHtml.replace("%bruttoSumme%", ""+df.format(brutto));

       PdfWriter writer = new PdfWriter(outputPath);
       PdfDocument document = new PdfDocument(writer);

       ConverterProperties converterProperties = new ConverterProperties();
       HtmlConverter.convertToDocument(replacedHtml, document, converterProperties);

       document.close();
   }


   private static String html = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
       "<!DOCTYPE html\n" +
       "        PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
       "        \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
       "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
       "<head>\n" +
       "    <title>Title</title>\n" +
       "    <style type=\"text/css\">\n" +
       "        .floatLeft{\n" +
       "            float:left;\n" +
       "        }\n" +
       "        .right{\n" +
       "            margin-left: 420px;\n" +
       "        }\n" +
       "        .bold{\n" +
       "            font-weight: bold;\n" +
       "        }\n" +
       "        table {\n" +
       "            width: 100%;\n" +
       "        }\n" +
       "        th {\n" +
       "            padding-top: 10px;\n" +
       "            padding-bottom: 10px;\n" +
       "            font-weight: bold;\n" +
       "            text-align: left;\n" +
       "        }\n" +
       "        table{\n" +
       "            border-collapse: collapse;\n" +
       "        }\n" +
       "        table thead tr th{\n" +
       "            border-bottom: 1px solid #9c9c9c;\n" +
       "        }\n" +
       "        .footer {\n" +
       "            margin-top: 60px;\n" +
       "            font-weight: lighter;\n" +
       "            color: #9c9c9c;\n" +
       "        }\n" +
       "        .items {\n" +
       "            height: 350px;\n" +
       "        }\n" +
       "        tr {\n" +
       "            vertical-align: top;\n" +
       "            height: fit-content;\n" +
       "            margin: 5px;\n" +
       "        }\n" +
       "    </style>\n" +
       "</head>\n" +
       "<body>\n" +
       "\n" +
       "    <h1>Rechnung</h1>\n" +
       "    <div>\n" +
       "        Firma <br/>\n" +
       "        Muster Branche GmbH<br/>\n" +
       "        Max Musterman<br/>\n" +
       "        Musterstraße 34<br/>\n" +
       "        4567 Musterdorf\n" +
       "    </div>\n" +
       "    <div class=\"right bold\">\n" +
       "        Rechnungsdatum: %nowDate%<br/>\n" +
       "        Liefer- bzw. Leistungsdatum: %performanceDate%<br/>\n" +
       "        Ust-IdNr.: 12345678<br/>\n" +
       "    </div>\n" +
       "    <div>\n" +
       "        <br/>\n" +
       "        <h3>Rechnungs Nr.: %invoiceNr%</h3>\n" +
       "        <br/>\n" +
       "        <p>Sehr Geehrte Damen und Herren</p>\n" +
       "        <br/>\n" +
       "        <p>\n" +
       "            Wir bedanken uns für Ihren Einkauf und stellen Ihnen hiermit für folgende Leistungen die Rechnung zur Verfügung:\n" +
       "        </p>\n" +
       "        <br/>\n" +
       "        <table class=\"items\">\n" +
       "            <thead>\n" +
       "            <tr>\n" +
       "                <th>Beschreibung</th>\n" +
       "                <th>Menge</th>\n" +
       "                <th>Preis</th>\n" +
       "                <th>Netto</th>\n" +
       "            </tr>\n" +
       "            </thead>\n" +
       "            <tbody>\n" +
       "            %rows%\n" +
       "            <tr>\n" +
       "                <td> </td>\n" +
       "                <td> </td>\n" +
       "                <td>Nettobetrag:</td>\n" +
       "                <td>%nettoSumme%€</td>\n" +
       "            </tr>\n" +
       "            <tr>\n" +
       "                <td> </td>\n" +
       "                <td> </td>\n" +
       "                <td>Ust. 20%:</td>\n" +
       "                <td>%ust%€</td>\n" +
       "            </tr>\n" +
       "            <tr>\n" +
       "                <td> </td>\n" +
       "                <td> </td>\n" +
       "                <td>Rechnungssumme:</td>\n" +
       "                <td>%bruttoSumme%€</td>\n" +
       "            </tr>\n" +
       "\n" +
       "            </tbody>\n" +
       "        </table>\n" +
       "\n" +
       "    </div>\n" +
       "    <div class=\"footer\">\n" +
       "        <table>\n" +
       "            <thead>\n" +
       "                <tr>\n" +
       "                    <td>&nbsp;</td>\n" +
       "                    <td>&nbsp;</td>\n" +
       "                    <td>&nbsp;</td>\n" +
       "                </tr>\n" +
       "            </thead>\n" +
       "            <tbody>\n" +
       "                <tr>\n" +
       "                    <td>Mustermann GmbH</td>\n" +
       "                    <td>Tel.: 0123456789</td>\n" +
       "                    <td>Musterbank Musterstadt</td>\n" +
       "                </tr>\n" +
       "                <tr>\n" +
       "                    <td>Musterstraße 1</td>\n" +
       "                    <td>E-Mail:</td>\n" +
       "                    <td>BLZ: 43527</td>\n" +
       "                </tr>\n" +
       "                <tr>\n" +
       "                    <td>0123 Musterstadt</td>\n" +
       "                    <td>max.mustermann@gmail.com</td>\n" +
       "                    <td>IBAN: AT283712893712873213</td>\n" +
       "                </tr>\n" +
       "                <tr>\n" +
       "                    <td>&nbsp;</td>\n" +
       "                    <td>Web: www.maxmusterman.com</td>\n" +
       "                    <td>BIC: SDMANDJSA13123</td>\n" +
       "                </tr>\n" +
       "            </tbody>\n" +
       "        </table>\n" +
       "    </div>\n" +
       "    <table>\n" +
       "    </table>\n" +
       "\n" +
       "</body>\n" +
       "</html>";

   private static final String row =
        "            <tr>\n" +
        "                <td>%description%</td>\n" +
        "                <td>%amount%</td>\n" +
        "                <td>%price%</td>\n" +
        "                <td>%netto%</td>\n" +
        "            </tr>\n";
}
