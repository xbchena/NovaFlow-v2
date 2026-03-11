import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  ActivityIndicator,
  Alert,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { authService } from '../../api/auth';

type Props = NativeStackScreenProps<any, 'Login'>;

export const LoginScreen: React.FC<Props> = ({ navigation }) => {
  const [loading, setLoading] = useState(false);

  const handleWeChatLogin = async () => {
    setLoading(true);
    try {
      const result = await authService.loginWithWeChat();

      if (result.success) {
        navigation.replace('Main');
      } else {
        Alert.alert('登录失败', result.message || '微信登录失败，请重试');
      }
    } catch (error) {
      Alert.alert('登录失败', '网络错误，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  const handlePhoneLogin = () => {
    navigation.navigate('PhoneLogin');
  };

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        {/* Logo */}
        <View style={styles.logoContainer}>
          <Text style={styles.logoIcon}>🎬</Text>
          <Text style={styles.logoText}>NovaFlow</Text>
          <Text style={styles.slogan}>今天吃什么？AI来帮你</Text>
        </View>

        {/* Login Buttons */}
        <View style={styles.buttonsContainer}>
          <TouchableOpacity
            style={[styles.loginButton, styles.wechatButton]}
            onPress={handleWeChatLogin}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <>
                <Text style={styles.wechatIcon}>💬</Text>
                <Text style={styles.buttonText}>微信登录</Text>
              </>
            )}
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.loginButton, styles.phoneButton]}
            onPress={handlePhoneLogin}
          >
            <Text style={styles.phoneIcon}>📱</Text>
            <Text style={[styles.buttonText, styles.phoneButtonText]}>手机号登录</Text>
          </TouchableOpacity>
        </View>

        {/* Terms */}
        <Text style={styles.terms}>
          登录即表示同意《用户协议》和《隐私政策》
        </Text>
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>NovaFlow v1.0.0</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 40,
  },
  logoContainer: {
    alignItems: 'center',
    marginBottom: 60,
  },
  logoIcon: {
    fontSize: 64,
    marginBottom: 20,
  },
  logoText: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 10,
  },
  slogan: {
    fontSize: 16,
    color: '#666',
  },
  buttonsContainer: {
    gap: 15,
  },
  loginButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 16,
    borderRadius: 12,
    gap: 10,
  },
  wechatButton: {
    backgroundColor: '#07C160',
  },
  phoneButton: {
    backgroundColor: '#fff',
    borderWidth: 1.5,
    borderColor: '#007AFF',
  },
  wechatIcon: {
    fontSize: 24,
  },
  phoneIcon: {
    fontSize: 24,
  },
  buttonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#fff',
  },
  phoneButtonText: {
    color: '#007AFF',
  },
  terms: {
    fontSize: 12,
    color: '#999',
    textAlign: 'center',
    marginTop: 30,
  },
  footer: {
    paddingVertical: 20,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 12,
    color: '#999',
  },
});
