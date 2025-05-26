# 加载必要的 R 包
library(vegan)       # 生态学分析
library(readxl)      # 读取 Excel
library(openxlsx)    # 写入 Excel
library(ggplot2)     # 可视化
library(magrittr)
library(tidyverse)

# 获取命令行参数
args <- commandArgs(trailingOnly = TRUE)
input_file <- args[1]
output_file <- args[2]

# 输出图像路径
output_plot <- sub("\\.xlsx$", "_beta_plot.png", output_file)

# 读取 OTU 表格
otu_data <- read_excel(input_file)

# 设置行名为 OTU ID，删除第一列
rownames(otu_data) <- otu_data$`#OTU ID`
otu_data <- otu_data[, -1]

# 转置为 样本 × OTU
otu_matrix <- t(as.matrix(otu_data))

# 计算 Bray-Curtis 距离矩阵
bray_dist <- vegdist(otu_matrix, method = "bray")

# 使用 PCoA（Principal Coordinates Analysis）进行降维
pcoa_result <- cmdscale(bray_dist, k = 2, eig = TRUE)

# 获取坐标
pcoa_points <- as.data.frame(pcoa_result$points)
colnames(pcoa_points) <- c("PCoA1", "PCoA2")
pcoa_points$Sample <- rownames(pcoa_points)

# 写入 Beta 距离矩阵
dist_matrix <- as.matrix(bray_dist)
write.xlsx(as.data.frame(dist_matrix), output_file)

# 绘图
p <- ggplot(pcoa_points, aes(x = PCoA1, y = PCoA2, label = Sample)) +
  geom_point(color = "#1f77b4", size = 4) +
  geom_text(vjust = -1, hjust = 1.1) +
  theme_minimal(base_size = 14) +
  labs(title = "Beta Diversity (PCoA based on Bray-Curtis)",
       x = "PCoA1",
       y = "PCoA2")

# 保存图像
ggsave(filename = output_plot, plot = p, width = 8, height = 6)

cat("Beta diversity analysis completed.\n")
cat("Distance matrix saved to:", output_file, "\n")
cat("PCoA plot saved to:", output_plot, "\n")
