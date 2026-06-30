import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  Alert,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

import { MainStackParamList } from '../../../App';
import { videoService } from '../../api';

type Props = NativeStackScreenProps<MainStackParamList, 'UploadProgress'>;

export const UploadProgressScreen: React.FC<Props> = ({ route, navigation }) => {
  const { videoUri, location } = route.params;
  const [stage, setStage] = useState<'uploading' | 'analyzing' | 'done'>('uploading');
  const [progress, setProgress] = useState(0);
  const [videoId, setVideoId] = useState<string | null>(null);

  useEffect(() => {
    handleUpload();
  }, []);

  const handleUpload = async () => {
    try {
      // Upload video
      const result = await videoService.uploadVideo(
        videoUri,
        30, // duration - in real app, get this from video metadata
        location || undefined,
        (p) => setProgress(p)
      );

      if (result.success && result.videoId) {
        setVideoId(result.videoId);
        setStage('analyzing');

        // Wait for analysis
        const analysisResult = await videoService.waitForAnalysis(
          result.videoId,
          (status) => {
            // Update progress during analysis
            setProgress(Math.min(80 + (status.length % 20), 99));
          }
        );

        if (analysisResult.success && analysisResult.data && result.videoId) {
          setStage('done');
          setProgress(100);

          setTimeout(() => {
            navigation.replace('Recommendation', {
              videoId: result.videoId as string,
              analysisData: analysisResult.data as NonNullable<typeof analysisResult.data>,
            });
          }, 500);
        } else {
          throw new Error(analysisResult.message || '分析失败');
        }
      } else {
        throw new Error(result.message || '上传失败');
      }
    } catch (error) {
      Alert.alert('错误', '视频上传或分析失败，请重试', [
        {
          text: '重试',
          onPress: () => handleUpload(),
        },
        {
          text: '取消',
          onPress: () => navigation.goBack(),
        },
      ]);
    }
  };

  const getStageText = () => {
    switch (stage) {
      case 'uploading':
        return '上传视频中...';
      case 'analyzing':
        return 'AI分析中...';
      case 'done':
        return '完成！';
    }
  };

  const getProgressPercent = () => {
    switch (stage) {
      case 'uploading':
        return Math.floor(progress * 0.7); // 0-70%
      case 'analyzing':
        return 70 + Math.floor(progress * 0.28); // 70-98%
      case 'done':
        return 100;
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <ActivityIndicator size="large" color="#007AFF" />

        <Text style={styles.title}>{getStageText()}</Text>

        <View style={styles.progressContainer}>
          <View style={styles.progressBar}>
            <View
              style={[styles.progressFill, { width: `${getProgressPercent()}%` }]}
            />
          </View>
          <Text style={styles.progressText}>{getProgressPercent()}%</Text>
        </View>

        <Text style={styles.message}>
          {stage === 'uploading' && '请稍候，正在上传您的视频...'}
          {stage === 'analyzing' && 'AI正在分析场景并生成推荐...'}
        </Text>

        {stage === 'analyzing' && (
          <View style={styles.tips}>
            <Text style={styles.tipText}>💡 小提示</Text>
            <Text style={styles.tipText}>我们会根据场景推荐附近的美食</Text>
          </View>
        )}
      </View>

      <TouchableOpacity
        style={styles.cancelButton}
        onPress={() => navigation.goBack()}
      >
        <Text style={styles.cancelButtonText}>取消</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
    paddingHorizontal: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 30,
    marginBottom: 20,
  },
  progressContainer: {
    width: '100%',
    alignItems: 'center',
    marginBottom: 20,
  },
  progressBar: {
    width: '100%',
    height: 8,
    backgroundColor: '#E5E5E5',
    borderRadius: 4,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#007AFF',
  },
  progressText: {
    fontSize: 16,
    fontWeight: '600',
    marginTop: 10,
  },
  message: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
  },
  tips: {
    marginTop: 40,
    padding: 15,
    backgroundColor: '#F0F9FF',
    borderRadius: 12,
  },
  tipText: {
    fontSize: 14,
    color: '#007AFF',
    textAlign: 'center',
    marginBottom: 5,
  },
  cancelButton: {
    position: 'absolute',
    bottom: 50,
  },
  cancelButtonText: {
    fontSize: 16,
    color: '#666',
  },
});
