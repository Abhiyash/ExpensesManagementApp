package com.apm.expenses.service;

import com.apm.expenses.constant.Constants;
import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.dto.TotalDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpensesService {

    @Autowired
    StatementDetailsDao statementDetailsDao;

    @Autowired
    StatementService statementService;

    public List<TotalDto> calculateExpenses(String userId, LocalDate fromDate, LocalDate toDate) {
        // TODO
        // 1. Fetch statements
        // 2. Calculate total for category and subcategory
        try {
            List<BankStatementDetailsDto> bankStatementDetailsDtoList = statementService.getStatement(userId, fromDate,
                    toDate);
            Map<String, List<TotalDto>> totalMap = new HashMap<>();
            List<TotalDto> totalDtoList = new ArrayList<>();
            for (BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList) {
                addOrUpdateTotal(totalMap, bankStatementDetailsDto.getCategory(),
                        bankStatementDetailsDto.getDebitAmount(), "Category",
                        bankStatementDetailsDto.getExpenseMonth());
                addOrUpdateTotal(totalMap, bankStatementDetailsDto.getSubCategory(),
                        bankStatementDetailsDto.getDebitAmount(), "SubCategory",
                        bankStatementDetailsDto.getExpenseMonth());
            }
            for (List<TotalDto> list : totalMap.values()) {
                totalDtoList.addAll(list);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(totalDtoList);
            FileOutputStream fileOutputStream = new FileOutputStream("expenses.json");
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
            System.out.println(totalDtoList);
            System.out.println(totalDtoList);
            createPdf(totalDtoList, userId, fromDate, toDate);
            return totalDtoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createPdf(List<TotalDto> totalDtoList, String userId, LocalDate fromDate, LocalDate toDate) {
        // TODO
        // 1. Create Category Chart
        JFreeChart chart1 = createChart(totalDtoList, "Category");
        JFreeChart chart2 = createChart(totalDtoList, "SubCategory");
        // 2. Create SubCategory Chart
        // 3. Export them in PDF.

        Document document = new Document();
        try {
            String path = Constants.APP_FILES_PATH + File.separator + "AbhiSmrutee" + File.separator + "output_files";
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = userId + "_" + toDate + "_" + fromDate + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
            document.open();
            addChartToPDF(chart1, document);
            addChartToPDF(chart2, document);
            document.close();
            System.out.println("PDF Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addChartToPDF(JFreeChart chart, Document document) {
        int width = 1000;
        int height = 700;
        try {
            BufferedImage chartImage = chart.createBufferedImage(width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", byteArrayOutputStream);

            Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
            // Scale image to fit page width if necessary, leaving some margin (90% of
            // available width)
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin())
                    / image.getWidth()) * 90;
            image.scalePercent(scaler);
            image.setAlignment(Image.MIDDLE);
            document.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JFreeChart createChart(List<TotalDto> totalDtoList, String name) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (TotalDto totalDto : totalDtoList) {
            if (totalDto.getType().equals(name)) {
                dataset.addValue(totalDto.getAmount(), name, totalDto.getName());
            }
        }
        JFreeChart chart = ChartFactory.createBarChart(name, name, "Amount", dataset, PlotOrientation.VERTICAL, true,
                true, true);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        return chart;
    }

    private void addOrUpdateTotal(Map<String, List<TotalDto>> totalDtoMap, String name, double amount, String type,
            String expenseMonth) {
        if (totalDtoMap.containsKey(expenseMonth)) {
            List<TotalDto> totalDtoList = totalDtoMap.get(expenseMonth);
            for (TotalDto totalDto : totalDtoList) {
                if (totalDto.getName().equals(name)) {
                    totalDto.setAmount(totalDto.getAmount() + amount);
                    return;
                }
            }
            totalDtoList.add(TotalDto.builder().name(name).amount(amount).type(type).build());
        } else {
            totalDtoMap.put(expenseMonth,
                    new ArrayList<>(List.of(TotalDto.builder().name(name).amount(amount).type(type).build())));
        }

    }
}
