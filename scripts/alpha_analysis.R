# 加载必要的 R 包
library(vegan)       # 生态学分析
library(readxl)      # 读取 Excel
library(openxlsx)    # 写入 Excel
library(magrittr)
library(tidyverse)

# 获取命令行参数
args <- commandArgs(trailingOnly = TRUE)
input_file <- args[1]
output_file <- args[2]

# 如果要保存图像，设定图像文件路径（与 output_file 同目录）
output_plot <- sub("\\.xlsx$", "_alpha_plot.png", output_file)

# 读取 OTU 表格
otu_data <- read_excel(input_file)

# 设置行名为 OTU ID，删除第一列
rownames(otu_data) <- otu_data$`#OTU ID`
otu_data <- otu_data[, -1]

# 转置为 样本 × OTU
otu_matrix <- t(as.matrix(otu_data))

# 计算 Alpha 多样性指标
shannon_index <- diversity(otu_matrix, index = "shannon")
simpson_index <- diversity(otu_matrix, index = "simpson")
chao1_index <- estimateR(otu_matrix)[2, ]
observed_species <- rowSums(otu_matrix > 0)
goods_coverage <- 1 - (otu_matrix == 1) %>% rowSums() / rowSums(otu_matrix > 0)

# 整理结果为 DataFrame
result <- data.frame(
  Sample = rownames(otu_matrix),
  Shannon = shannon_index,
  Simpson = simpson_index,
  Chao1 = chao1_index,
  Observed_Species = observed_species,
  Goods_Coverage = round(goods_coverage, 4)
)

# 写入分析结果 Excel
write.xlsx(result, output_file)

# 绘图：可视化多个 Alpha 指标（使用 pivot_longer 转换）
result_long <- result %>%
  pivot_longer(cols = -Sample, names_to = "Index", values_to = "Value")

# 使用 ggplot2 画出箱线图
p <- ggplot(result_long, aes(x = Index, y = Value)) +
  geom_boxplot(fill = "#69b3a2", color = "black") +
  geom_jitter(width = 0.2, alpha = 0.7, color = "darkblue") +
  theme_minimal(base_size = 14) +
  labs(title = "Alpha Diversity Indices",
       x = "Diversity Index",
       y = "Value")

# 保存图像
ggsave(filename = output_plot, plot = p, width = 10, height = 6)

cat("Alpha diversity analysis completed.\n")
cat("Results saved to:", output_file, "\n")
cat("Plot saved to:", output_plot, "\n")
