from fastapi import FastAPI
from pydantic import BaseModel
import pickle
import jieba

# 初始化FastAPI应用
app = FastAPI(title="校园二手交易AI违规检测服务", version="1.0")

# 加载训练好的模型
with open("model.pkl", "rb") as f:
    vec, model = pickle.load(f)

# 复用原有的分词函数
def cut_words(text):
    return " ".join(jieba.lcut(text))

# 定义接口请求体
class TextCheckRequest(BaseModel):
    content: str  # 商品标题+描述的文本内容

# 核心接口：违规内容检测
@app.post("/ai/check", summary="商品文本违规检测")
def check_violation(req: TextCheckRequest):
    """
    接收商品标题/描述文本，调用AI模型检测是否违规
    返回：是否违规、违规提示、置信度
    """
    
    text_cut = cut_words(req.content)
    # 用加载好的模型做预测
    vec_text = vec.transform([text_cut])
    pred = model.predict(vec_text)[0]
    # 获取预测概率，提升结果可读性
    prob = model.predict_proba(vec_text)[0][1]  # 违规类的概率

    # 封装返回结果
    if pred == 1:
        return {
            "code": 200,
            "is_violation": True,
            "message": "检测到违规内容（代做/代写/代考等），禁止发布",
            "confidence": round(float(prob), 4)
        }
    else:
        return {
            "code": 200,
            "is_violation": False,
            "message": "内容合规，可正常发布",
            "confidence": round(float(prob), 4)
        }

# 检查接口
@app.get("/ai/health", summary="服务健康检查")
def health_check():
    return {"code": 200, "status": "AI服务正常运行"}