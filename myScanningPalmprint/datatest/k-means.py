import numpy as np
from sklearn.cluster import KMeans
import matplotlib.pyplot as plt
import seaborn as sns

# 读取数据
data1 = np.loadtxt('matched_output.txt')
data2 = np.loadtxt('not_matched_output.txt')

# 合并数据
data = np.concatenate((data1, data2))

# 添加随机数
rand_nums = np.random.rand(data.shape[0], 1)
data = np.hstack((data.reshape(-1, 1), rand_nums))

# 转换数据形状
X = data.reshape(-1, 2)

# 设置K-means参数
k = 2  # 期望的聚类数量，即两组数据
max_iters = 100  # 最大迭代次数

# 创建并拟合K-means模型
kmeans = KMeans(n_clusters=k, max_iter=max_iters)
kmeans.fit(X)

# 获取聚类中心
centers = kmeans.cluster_centers_

# 获取每个数据点的标签
labels = kmeans.labels_

# 确定阈值位置
threshold = min(centers[:, 0])
print("阈值为：", threshold)

# 可视化结果
plt.scatter(X[labels==0, 1], X[labels==0, 0], c='b', label='matched_data', s=5)
plt.scatter(X[labels==1, 1], X[labels==1, 0], c='r', label='not_matched_data', s=5)
plt.axhline(y=threshold, c='g', label='Threshold')

# 显示阈值信息
annotation_text = 'Threshold = %.5f' % threshold
plt.annotate(annotation_text, 
            xy=(0.85, 1.0), 
            xytext=(0.85, 1.0),
            xycoords='axes fraction', 
            fontsize=7, 
            ha='center')

plt.legend()
plt.xlabel('Random Number')
plt.ylabel('Data Value')
plt.savefig('k-means_plot.png')   # 添加保存图片代码
plt.show()