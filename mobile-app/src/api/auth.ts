import { apiClient } from './client';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { openURL } from 'expo-linking';
import { WebBrowser } from 'expo-web-browser';
import * as AuthSession from 'expo-auth-session';

export interface User {
  id: string;
  openid: string;
  nickname: string;
  avatar?: string;
  phone?: string;
  createdAt: string;
}

export interface WeChatAuthResult {
  success: boolean;
  token?: string;
  refreshToken?: string;
  user?: User;
  message?: string;
}

export class AuthService {
  private readonly STORAGE_KEYS = {
    TOKEN: 'authToken',
    REFRESH_TOKEN: 'refreshToken',
    USER_INFO: 'userInfo',
  };

  /**
   * 微信登录
   */
  async loginWithWeChat(): Promise<WeChatAuthResult> {
    try {
      // 使用微信 SDK 或 WebView 进行微信授权
      // 这里提供两种实现方式

      // 方式1: 使用微信 SDK (需要集成微信SDK)
      // const result = await WeChat.sendAuthRequest('snsapi_userinfo');

      // 方式2: 使用 WebView + 微信开放平台
      const authUrl = `https://open.weixin.qq.com/connect/qrconnect?appid=${process.env.WECHAT_APP_ID}&redirect_uri=${encodeURIComponent('https://api.novaflow.com/api/v1/auth/wechat/callback')}&response_type=code&scope=snsapi_userinfo#wechat_redirect`;

      // 打开浏览器进行微信授权
      const result = await WebBrowser.openAuthSessionAsync(
        authUrl,
        'novaflow://auth'
      );

      if (result.type === 'success') {
        // 解析回调参数
        const urlParams = new URLSearchParams(result.url.split('?')[1]);
        const code = urlParams.get('code');

        if (code) {
          return this.handleWeChatCallback(code);
        }
      }

      return { success: false, message: '微信授权失败' };
    } catch (error) {
      console.error('WeChat login error:', error);
      return { success: false, message: '微信登录失败' };
    }
  }

  /**
   * 处理微信回调
   */
  private async handleWeChatCallback(code: string): Promise<WeChatAuthResult> {
    try {
      const response = await apiClient.post('/auth/wechat/callback', { code });

      if (response.success && response.data) {
        const { token, refreshToken, user } = response.data;

        // 存储认证信息
        await AsyncStorage.multiSet([
          [this.STORAGE_KEYS.TOKEN, token],
          [this.STORAGE_KEYS.REFRESH_TOKEN, refreshToken],
          [this.STORAGE_KEYS.USER_INFO, JSON.stringify(user)],
        ]);

        return {
          success: true,
          token,
          refreshToken,
          user,
        };
      }

      return { success: false, message: response.message || '登录失败' };
    } catch (error) {
      console.error('Handle WeChat callback error:', error);
      return { success: false, message: '处理微信授权失败' };
    }
  }

  /**
   * 手机号登录
   */
  async loginWithPhone(phone: string, code: string): Promise<WeChatAuthResult> {
    try {
      const response = await apiClient.post('/auth/phone/login', { phone, code });

      if (response.success && response.data) {
        const { token, refreshToken, user } = response.data;

        await AsyncStorage.multiSet([
          [this.STORAGE_KEYS.TOKEN, token],
          [this.STORAGE_KEYS.REFRESH_TOKEN, refreshToken],
          [this.STORAGE_KEYS.USER_INFO, JSON.stringify(user)],
        ]);

        return {
          success: true,
          token,
          refreshToken,
          user,
        };
      }

      return { success: false, message: response.message || '登录失败' };
    } catch (error) {
      return { success: false, message: '登录失败' };
    }
  }

  /**
   * 发送验证码
   */
  async sendVerificationCode(phone: string): Promise<boolean> {
    try {
      await apiClient.post('/auth/send-code', { phone });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * 退出登录
   */
  async logout(): Promise<void> {
    await AsyncStorage.multiRemove([
      this.STORAGE_KEYS.TOKEN,
      this.STORAGE_KEYS.REFRESH_TOKEN,
      this.STORAGE_KEYS.USER_INFO,
    ]);
  }

  /**
   * 获取当前用户信息
   */
  async getCurrentUser(): Promise<User | null> {
    try {
      const userInfo = await AsyncStorage.getItem(this.STORAGE_KEYS.USER_INFO);
      return userInfo ? JSON.parse(userInfo) : null;
    } catch {
      return null;
    }
  }

  /**
   * 获取访问令牌
   */
  async getAccessToken(): Promise<string | null> {
    return AsyncStorage.getItem(this.STORAGE_KEYS.TOKEN);
  }

  /**
   * 检查是否已登录
   */
  async isAuthenticated(): Promise<boolean> {
    const token = await this.getAccessToken();
    return !!token;
  }

  /**
   * 刷新令牌
   */
  async refreshToken(): Promise<boolean> {
    try {
      const refreshToken = await AsyncStorage.getItem(this.STORAGE_KEYS.REFRESH_TOKEN);
      if (!refreshToken) return false;

      const response = await apiClient.post('/auth/refresh', { refreshToken });

      if (response.success && response.data) {
        await AsyncStorage.setItem(this.STORAGE_KEYS.TOKEN, response.data.token);
        if (response.data.refreshToken) {
          await AsyncStorage.setItem(this.STORAGE_KEYS.REFRESH_TOKEN, response.data.refreshToken);
        }
        return true;
      }

      return false;
    } catch {
      return false;
    }
  }
}

export const authService = new AuthService();
