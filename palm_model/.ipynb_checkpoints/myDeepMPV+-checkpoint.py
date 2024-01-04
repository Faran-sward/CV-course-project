import numpy as np
import cv2 as cv
import math
import sys

import torch
import torch.nn as nn
from torchvision import transforms
from torchvision.transforms import InterpolationMode
from PIL import Image
from model import MobileFaceNet  # 导入MobileFaceNet模型的定义，确保你已经正确安装了它

from ultralytics import YOLO

sys.stdout.flush()

def getYOLOOutput(img, net):
    # Load a model
    model = YOLO(net)
    # Run batched inference on a list of images
    results = model(img)
    im = cv.imread(img, cv.IMREAD_COLOR)
    height, width, channels = im.shape
    dfg = [] #Double-finger-gap
    pc = []  #Palm-center
    for result in results:
        boxes = result.boxes
        for box in boxes:
            if box.cls == 1:
                pc.append([box.xywh[0][0],box.xywh[0][1],box.conf[0]])
            else:
                dfg.append([box.xywh[0][0],box.xywh[0][1],box.conf[0]])
    return dfg, pc

def onePoint(x, y, angle):
    X = x*math.cos(angle) + y*math.sin(angle)
    Y = y*math.cos(angle) - x*math.sin(angle)
    return [int(X), int(Y)]

def extractROI(img, dfg, pc):
    (H, W) = img.shape[:2]
    if W>H:
        im = np.zeros((W,W,3), np.uint8)
        im[...]=255
        im[1:H,1:W,:] = img[1:H,1:W,:]
        edge = W
    else:
        im = np.zeros((H,H,3), np.uint8)
        im[...]=255
        im[1:H,1:W,:] = img[1:H,1:W,:]
        edge = H
    
    center = (edge/2, edge/2)

    x1 = float(dfg[0][0])
    y1 = float(dfg[0][1])
    x2 = float(dfg[1][0])
    y2 = float(dfg[1][1])
    x3 = float(pc[0][0])
    y3 = float(pc[0][1])

    x0 = (x1+x2)/2
    y0 = (y1+y2)/2

    #指缝线段长度
    unitLen = math.sqrt(np.square(x2-x1)+np.square(y2-y1))

    #坐标系两个轴所在直线方程，并求解了原点的位置
    k1 = (y1-y2)/(x1-x2) # line AB
    b1 = y1-k1*x1

    k2 = (-1)/k1
    b2 = y3-k2*x3

    tmpX = (b2-b1)/(k1-k2)
    tmpY = k1*tmpX+b1

    #vec为掌心方向单位向量
    vec = [x3-tmpX, y3-tmpY]
    sidLen = math.sqrt(np.square(vec[0])+np.square(vec[1]))
    vec = [vec[0]/sidLen, vec[1]/sidLen]
    #print(vec)

    if vec[1]<0 and vec[0]>0: angle=-math.pi/2-math.acos(vec[0])
    elif vec[1]<0 and vec[0]<0: angle=math.acos(-vec[0])+math.pi/2
    elif vec[1]>=0 and vec[0]>0: angle=math.acos(vec[0])-math.pi/2
    else: angle = math.pi/2-math.acos(-vec[0])
    #print(angle/math.pi*180)

    x0, y0 = onePoint(x0-edge/2, y0-edge/2, angle)

    x0 += edge/2
    y0 += edge/2

    M = cv.getRotationMatrix2D(center, angle/math.pi*180, 1.0)
    tmp = cv.warpAffine(im, M, (edge, edge))
    ROI = tmp[int(y0+unitLen/4):int(y0+unitLen*11/4), int(x0-unitLen*5/4):int(x0+unitLen*5/4),:]
    ROI = cv.resize(ROI, (224, 224), interpolation=cv.INTER_CUBIC)
    return ROI

if __name__ == '__main__':

    '''
    Step 1: Get doble-finger-gaps and palm-center from YOLOV8 detecor
    '''
    # Load a model
    modelPath = '/root/palm_detector/palm_model/ScanningPalmprintbest.pt'
    imgPath1 = sys.argv[1]  # Input images
    imgPath2 = sys.argv[2]
    THRESHOLD = 0.5299

    dfg1, pc1 = getYOLOOutput(imgPath1, modelPath)
    dfg2, pc2 = getYOLOOutput(imgPath2, modelPath)

    '''
    Step 2: Construct the local coordinates based on detected points.
    '''

    # sys.stdout.write("111111111111111111111111111")
    print('----------------ROIcomplete--------------!')
    if len(dfg1)<2 or len(dfg2)<2:
        print('result3')
    else:
        print('start pear')
        if len(dfg1)>2:
            tmpdfg = []
            maxD = 0
            for i in range(len(dfg1)-1):
                for j in range(i+1, len(dfg1)):
                    d = math.sqrt(pow(dfg1[i][0]-dfg1[j][0],2)+pow(dfg1[i][1]-dfg1[j][1],2))
                    if d>maxD:
                        tmpdfg = [dfg1[i], dfg1[j]]
                        maxD = d
            dfg1 = tmpdfg
        if len(dfg2)>2:
            tmpdfg = []
            maxD = 0
            for i in range(len(dfg2)-1):
                for j in range(i+1, len(dfg2)):
                    d = math.sqrt(pow(dfg2[i][0]-dfg2[j][0],2)+pow(dfg2[i][1]-dfg2[j][1],2))
                    if d>maxD:
                        tmpdfg = [dfg2[i], dfg2[j]]
                        maxD = d
            dfg2 = tmpdfg
        pc1 = sorted(pc1, key=lambda x:x[-1], reverse=True)
        pc2 = sorted(pc2, key=lambda x:x[-1], reverse=True)
    
        img1 = cv.imread(imgPath1)
        img2 = cv.imread(imgPath2)

        ROI1 = extractROI(img1, dfg1, pc1)
        ROI2 = extractROI(img2, dfg2, pc2)

        # 加载预训练的MobileFaceNet模型
        model = MobileFaceNet()
        device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
        print('start')
        model.load_state_dict(torch.load("/root/palm_detector/palm_model/best.pth", map_location=device))
        print('end')
        # 将模型转移到 GPU 上
        model = nn.DataParallel(model, device_ids = [0])
        model.to(device)
        # model.load_state_dict(torch.load("best.pth", map_location=torch.device('cpu')))  # 加载预训练模型的权重
        model.eval()

        # 定义图像预处理的transform
        preprocess = transforms.Compose([
            transforms.Resize([224, 224], interpolation= InterpolationMode.NEAREST),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.5, 0.5, 0.5], std=[0.5, 0.5, 0.5])
        ])
        # 预处理第一张ROI图像
        roi_image1_rgb = cv.cvtColor(ROI1, cv.COLOR_BGR2RGB)
        roi_image1_pil = Image.fromarray(roi_image1_rgb)
        roi_image1_tensor = preprocess(roi_image1_pil).unsqueeze(0)
        # 预处理第二张ROI图像
        roi_image2_rgb = cv.cvtColor(ROI2, cv.COLOR_BGR2RGB)
        roi_image2_pil = Image.fromarray(roi_image2_rgb)
        roi_image2_tensor = preprocess(roi_image2_pil).unsqueeze(0)

        # 提取第一张ROI图像的特征向量
        with torch.no_grad():
            feat1 = model(roi_image1_tensor)
        # 提取第二张ROI图像的特征向量
        with torch.no_grad():
            feat2 = model(roi_image2_tensor)

        # 将特征向量转换为单位向量
        feat1_normalized = feat1 / torch.norm(feat1, dim=1, keepdim=True)
        feat2_normalized = feat2 / torch.norm(feat2, dim=1, keepdim=True)

        # 计算余弦相似度
        similarity = torch.cosine_similarity(feat1_normalized, feat2_normalized, dim=1)
        if similarity>=THRESHOLD: print('result1')
        else: print('result2')
    print('all completed')