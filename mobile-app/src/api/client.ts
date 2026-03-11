import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import appConfig from '../app.json';

const API_BASE_URL = appConfig.apiBaseUrl;

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor - add auth token
    this.client.interceptors.request.use(
      async (config: InternalAxiosRequestConfig) => {
        const token = await AsyncStorage.getItem('authToken');
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor - handle token refresh
    this.client.interceptors.response.use(
      (response) => response.data,
      async (error: AxiosError) => {
        if (error.response?.status === 401) {
          // Token expired, clear and redirect to login
          await AsyncStorage.multiRemove(['authToken', 'refreshToken', 'userInfo']);
          // TODO: Navigate to login
        }
        return Promise.reject(error.response?.data || error.message);
      }
    );
  }

  public get = (url: string, params?: any) => this.client.get(url, { params });
  public post = (url: string, data?: any) => this.client.post(url, data);
  public put = (url: string, data?: any) => this.client.put(url, data);
  public delete = (url: string) => this.client.delete(url);
  public patch = (url: string, data?: any) => this.client.patch(url, data);

  // Upload method for videos
  public upload = (url: string, formData: FormData, onProgress?: (progress: number) => void) => {
    return this.client.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(progress);
        }
      },
    });
  };
}

export const apiClient = new ApiClient();
