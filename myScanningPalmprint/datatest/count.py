import re

# 读取文件内容
with open("matched.txt", "r") as f:
    contents = f.read()

# 使用正则表达式匹配tensor
regex = r"tensor"
tensors = re.findall(regex, contents)

# 计算tensor个数
num_tensors = len(tensors)

print("该文件中tensor的个数为:", num_tensors)