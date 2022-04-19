package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.utils.InvoicePdfCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateRealData,test")
public class InvoicePdfCreatorTest {

    @Test
    public void canCorrectlyCreateInvoicePdf(){
        //InvoicePdfCreator.createPdf();
    }
}
