import re

in_filename = "matched.txt"
out_filename = "matched_output.txt"

# 读取文件内容
with open(in_filename, "r") as in_file:
    contents = in_file.read()

# 使用正则表达式提取浮点数
regex = r"tensor\(\[(-?\d+\.\d+)\]\)"
matches = re.findall(regex, contents)

# 将浮点数写入输出文件
with open(out_filename, "w") as out_file:
    for match in matches:
        out_file.write(match + "\n")

print("提取的浮点数已保存到", out_filename)