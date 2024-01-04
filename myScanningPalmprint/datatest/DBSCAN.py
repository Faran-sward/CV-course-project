import numpy as np
from sklearn.cluster import DBSCAN
import matplotlib.pyplot as plt
import seaborn as sns

# 读取数据
data1 = np.loadtxt('matched_output.txt')
data2 = np.loadtxt('not_matched_output.txt')

# 为数据生成随机数的第二维
data1_random = np.random.rand(len(data1))
data2_random = np.random.rand(len(data2))

# 将数据转换为二维数组
data1 = np.column_stack((data1, data1_random))
data2 = np.column_stack((data2, data2_random))

# 合并数据
data = np.concatenate((data1, data2), axis=0)

# 为不同的数据添加标签
labels = np.zeros(len(data), dtype=int)
labels[:len(data1)] = 1

# 训练DBSCAN模型
dbscan = DBSCAN(eps=0.5, min_samples=5)
dbscan.fit(data)

# 获取聚类标签和离群样本的索引
labels = dbscan.labels_
outliers_index = np.where(labels == -1)

# 获取两组数据的核心样本
core_samples_indices = dbscan.core_sample_indices_
core_samples = data[core_samples_indices]

# 计算阈值（取两组数据核心样本的均值）
threshold = np.mean(core_samples[:, 0])
print("阈值为：", threshold)

# 定义绘图函数
def plot_data():
    plt.scatter(data1[:, 0], data1[:, 1], color='blue', label='matched_data', s=5)
    plt.scatter(data2[:, 0], data2[:, 1], color='red', label='not_matched_data', s=5)
    # plt.scatter(core_samples[:, 0], core_samples[:, 1], color='green', label='core samples', s=20)
    plt.axvline(x=threshold, color='black', linestyle='--', label='Threshold')

    # 显示阈值信息
    annotation_text = 'Threshold = %.5f' % threshold
    plt.annotate(annotation_text, 
                xy=(0.85, 1.0), 
                xytext=(0.85, 1.0),
                xycoords='axes fraction', 
                fontsize=7, 
                ha='center')

    plt.legend(loc='best')
    plt.title('DBSCAN Clustering')
    plt.xlabel('Data')
    plt.savefig('dbscan_plot.png')   # 添加保存图片
    plt.show()

plot_data()