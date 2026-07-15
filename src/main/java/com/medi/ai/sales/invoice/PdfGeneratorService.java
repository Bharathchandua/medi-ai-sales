package com.medi.ai.sales.invoice;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
public class PdfGeneratorService {

    public ByteArrayInputStream generateInvoicePdf(Invoice invoice) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("Medi-AI-Sales Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Invoice details
            Paragraph invoiceDetails = new Paragraph();
            invoiceDetails.setFont(bodyFont);
            invoiceDetails.add("Invoice Number: " + invoice.getInvoiceNumber() + "\n");
            invoiceDetails.add("Invoice Date: " + invoice.getInvoiceDate() + "\n\n");
            document.add(invoiceDetails);

            // Customer details
            Paragraph customerDetails = new Paragraph();
            customerDetails.setFont(bodyFont);
            customerDetails.add("Billed To:\n");
            customerDetails.add("Name: " + invoice.getCustomer().getName() + "\n");
            customerDetails.add("Phone: " + invoice.getCustomer().getPhone() + "\n");
            customerDetails.add("Email: " + (invoice.getCustomer().getEmail() != null ? invoice.getCustomer().getEmail() : "N/A") + "\n");
            customerDetails.add("Address: " + (invoice.getCustomer().getAddress() != null ? invoice.getCustomer().getAddress() : "N/A") + "\n\n");
            document.add(customerDetails);

            // Items Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            // Table Headers
            String[] headers = {"Product Name", "Qty", "Rate", "GST %", "Discount %", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Table Body Rows
            for (InvoiceItem item : invoice.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), bodyFont)));
                
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);

                PdfPCell rateCell = new PdfPCell(new Phrase(item.getRate().toString(), bodyFont));
                rateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(rateCell);

                PdfPCell gstCell = new PdfPCell(new Phrase(item.getGstPercentage().toString() + "%", bodyFont));
                gstCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(gstCell);

                PdfPCell discCell = new PdfPCell(new Phrase(item.getDiscountPercentage().toString() + "%", bodyFont));
                discCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(discCell);

                PdfPCell totalCell = new PdfPCell(new Phrase(item.getTotalAmount().toString(), bodyFont));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);

            // Summary Section
            Paragraph summary = new Paragraph();
            summary.setFont(bodyFont);
            summary.setAlignment(Element.ALIGN_RIGHT);
            summary.setSpacingBefore(15);
            summary.add("Subtotal: " + invoice.getSubTotal() + "\n");
            summary.add("Total GST: " + invoice.getTotalGst() + "\n");
            summary.add("Total Discount: " + invoice.getTotalDiscount() + "\n");
            
            Paragraph grandTotal = new Paragraph("Grand Total: " + invoice.getGrandTotal(), bodyBold);
            grandTotal.setAlignment(Element.ALIGN_RIGHT);
            
            document.add(summary);
            document.add(grandTotal);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
