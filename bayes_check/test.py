import jieba
import pickle

def cut_words(text):
    return " ".join(jieba.lcut(text))

# 加载训练好的AI
with open("model.pkl", "rb") as f:
    vec, model = pickle.load(f)

# 测试
test_list = [
    "自用台式机",
    "代做课程设计",
    "卖二手耳机",
    "代写作业",
    "求代考高数",
    "出台电脑主机"
]

print("===== 测试结果 =====")
for text in test_list:
    vec_text = vec.transform([cut_words(text)])
    pred = model.predict(vec_text)[0]
    print(f"{text} → {pred}   {'违规' if pred==1 else '正常'}")