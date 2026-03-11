/**
 * NovaFlow 图标组件
 * 使用SVG创建统一的图标系统
 */

import React from 'react';
import { View, StyleSheet } from 'react-native';

interface IconProps {
  size?: number;
  color?: string;
  style?: any;
}

export const Icons = {
  // 导航图标
  Home: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M9 22V12H15V22" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Camera: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M23 19C23 19.5304 22.7893 20.0391 22.4142 20.4142C22.0391 20.7893 21.5304 21 21 21H3C2.46957 21 1.96086 20.7893 1.58579 20.4142C1.21071 20.0391 1 19.5304 1 19V8C1 7.46957 1.21071 6.96086 1.58579 6.58579C1.96086 6.21071 2.46957 6 3 6H7L9 3H15L17 6H21C21.5304 6 22.0391 6.21071 22.4142 6.58579C22.7893 6.96086 23 7.46957 23 8V19Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="12" cy="13" r="4" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Location: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M21 10C21 17 12 23 12 23C12 23 3 17 3 10C3 5.02944 7.02944 1 12 1C16.9706 1 21 5.02944 21 10Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="12" cy="10" r="3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  History: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M12 8V12L15 15" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="12" cy="12" r="9" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M12 3V1M12 23V21M3 12H1M23 12H21" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Settings: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <circle cx="12" cy="12" r="3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M19.4 15C19.2669 15.3016 19.2272 15.6362 19.286 15.96C19.3448 16.2838 19.4995 16.5817 19.73 16.82L19.79 16.88C19.976 17.0657 20.1202 17.2893 20.2134 17.5357C20.3066 17.7821 20.3466 18.0452 20.3306 18.3082C20.3146 18.5712 20.243 18.8276 20.1206 19.0606C19.9982 19.2936 19.8283 19.4976 19.62 19.66L19.56 19.72C19.3743 19.906 19.2741 20.1576 19.2741 20.42C19.2741 20.6824 19.3743 20.934 19.56 21.12L19.62 21.18C19.8057 21.366 19.9732 21.579 20.1165 21.8125C20.2599 22.0459 20.3773 22.2966 20.4652 22.5582C20.553 22.8198 20.6102 23.0891 20.6349 23.3616C20.6597 23.6341 20.6516 23.9063 20.611 24.1762C20.6516 23.9063 20.6597 23.6341 20.6349 23.3616C20.6102 23.0891 20.553 22.8198 20.4652 22.5582C20.3773 22.2966 20.2599 22.0459 20.1165 21.8125C19.9732 21.579 19.8057 21.366 19.62 21.18L19.56 21.12C19.3743 20.934 19.2741 20.6824 19.2741 20.42C19.2741 20.1576 19.3743 19.906 19.56 19.72L19.62 19.66C19.8057 19.474 19.9732 19.261 20.1165 19.0275C20.2599 18.7941 20.3773 18.5434 20.4652 18.2818C20.553 18.0202 20.6102 17.7509 20.6349 17.4784C20.6597 17.2059 20.6516 16.9337 20.611 16.6638C20.6516 16.9337 20.6597 17.2059 20.6349 17.4784C20.6102 17.7509 20.553 18.0202 20.4652 18.2818C20.3773 18.5434 20.2599 18.7941 20.1165 19.0275C19.9732 19.261 19.8057 19.474 19.62 19.66L19.56 19.72C19.3743 19.906 19.1507 20.0502 18.9043 20.1434C18.6579 20.2366 18.3948 20.2766 18.1318 20.2606C17.8688 20.2446 17.6124 20.173 17.3794 20.0506C17.1464 19.9282 16.9424 19.7583 16.78 19.55V8.45C16.9424 8.24169 17.1464 8.07176 17.3794 7.94937C17.6124 7.82697 17.8688 7.75544 18.1318 7.73942C18.3948 7.72341 18.6579 7.76337 18.9043 7.85658C19.1507 7.9498 19.3743 8.09402 19.56 8.28L19.62 8.34C19.8057 8.526 19.9732 8.739 20.1165 8.9725C20.2599 9.2059 20.3773 9.4566 20.4652 9.7182C20.553 9.9798 20.6102 10.2491 20.6349 10.5216C20.6597 10.7941 20.6516 11.0663 20.611 11.3362C20.6516 11.0663 20.6597 10.7941 20.6349 10.5216C20.6102 10.2491 20.553 9.9798 20.4652 9.7182C20.3773 9.4566 20.2599 9.2059 20.1165 8.9725C19.9732 8.739 19.8057 8.526 19.62 8.34L19.56 8.28C19.3743 8.094 19.1507 7.9498 18.9043 7.85658C18.6579 7.76337 18.3948 7.72341 18.1318 7.73942C17.8688 7.75544 17.6124 7.82697 17.3794 7.94937C17.1464 8.07176 16.9424 8.24169 16.78 8.45V15Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Close: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M18 6L6 18M6 6L18 18" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Check: ({ size = 24, color = '#34C759', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M20 6L9 17L4 12" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Heart: ({ size = 24, color = '#FF3B30', filled = false, style }: IconProps & { filled?: boolean }) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? color : 'none'}>
        <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04096 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L4.22 13.45L12 21.23L19.78 13.45L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22248 22.4518 8.5C22.4518 7.77752 22.3095 7.0621 22.0329 6.39464C21.7563 5.72718 21.351 5.12084 20.84 4.61V4.61Z" stroke={filled ? 'none' : color} strokeWidth={filled ? '0' : '2'} strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Share: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <circle cx="18" cy="5" r="3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="6" cy="12" r="3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="18" cy="19" r="3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M8.59 13.51L15.42 17.49M15.41 6.51L8.59 10.49" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Star: ({ size = 24, color = '#FF9500', filled = false, style }: IconProps & { filled?: boolean }) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? color : 'none'}>
        <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke={filled ? 'none' : color} strokeWidth={filled ? '0' : '2'} strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  ArrowRight: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Refresh: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M23 4V10H17" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M20.49 15C19.9828 16.8537 18.9025 18.4946 17.3988 19.6793C15.8951 20.8641 14.0451 21.5368 12.1343 21.6025C10.2235 21.6682 8.33363 21.1237 6.75593 20.0456C5.17823 18.9675 4.00108 17.4112 3.39834 15.6108C2.7956 13.8105 2.79904 11.8623 3.40814 10.0641C4.01724 8.26585 5.2003 6.71369 6.78182 5.64068C8.36333 4.56768 10.2549 4.02923 12.1654 4.10092C14.0758 4.1726 15.9234 4.85089 17.4238 6.04L20.49 9.105" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Filter: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M22 3H2L10 12.46V19L14 21V12.46L22 3Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Search: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <circle cx="11" cy="11" r="8" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M21 21L16.65 16.65" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Bell: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M18 8C18 6.4087 17.3679 4.88258 16.2426 3.75736C15.1174 2.63214 13.5913 2 12 2C10.4087 2 8.88258 2.63214 7.75736 3.75736C6.63214 4.88258 6 6.4087 6 8C6 11.0903 5.22048 13.206 4.38933 14.6145C4.01587 15.2367 3.8291 15.5479 3.8291 15.5479C3.74239 15.6922 3.69665 15.8575 3.69665 16.026C3.69665 16.1945 3.74239 16.3598 3.8291 16.5041C3.91581 16.6484 4.0403 16.7657 4.18935 16.8436C4.33841 16.9216 4.5061 16.9569 4.67437 16.9456C4.84264 16.9344 5.00436 16.877 5.14184 16.7795C5.14184 16.7795 7.28118 15.2266 8.69796 14.4449C9.29244 14.1265 9.97477 13.9886 10.6513 14.0482C11.3278 14.1078 11.9746 14.3624 12.5181 14.7837C13.0617 15.205 13.4808 15.7757 13.7274 16.4302C13.974 17.0847 14.0384 17.7977 13.9133 18.4882" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M12 22C13.0609 22 14.0783 21.5786 14.8284 20.8284C15.5786 20.0783 16 19.0609 16 18C16 17.4696 15.7893 16.9609 15.4142 16.5858C15.0391 16.2107 14.5304 16 14 16H10C9.46957 16 8.96086 16.2107 8.58579 16.5858C8.21071 16.9609 8 17.4696 8 18C8 19.0609 8.42143 20.0783 9.17157 20.8284C9.92172 21.5786 10.9391 22 12 22Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  User: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M20 21C20 18.2386 17.7614 16 15 16H9C6.23858 16 4 18.2386 4 21" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="12" cy="7" r="4" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Edit: ({ size = 24, color = '#666', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M18.5 2.50001C18.8978 2.10219 19.4374 1.87869 20 1.87869C20.5626 1.87869 21.1022 2.10219 21.5 2.50001C21.8978 2.89784 22.1213 3.4374 22.1213 4.00001C22.1213 4.56262 21.8978 5.10219 21.5 5.50001L12 15L8 16L9 12L18.5 2.50001Z" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),

  Trash: ({ size = 24, color = '#FF3B30', style }: IconProps) => (
    <View style={[{ width: size, height: size }, style]}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <path d="M3 6H5H21" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M10 11V17" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M14 11V17" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </View>
  ),
};

// 骨架屏加载组件
export const Skeleton = ({ width, height, style }: { width?: number | string; height?: number | string; style?: any }) => (
  <View
    style={[
      {
        width: width || '100%',
        height: height || 20,
        backgroundColor: '#E5E5E5',
        borderRadius: 4,
      },
      style,
    ]}
  />
);

// 食物图片占位符组件
export const FoodImagePlaceholder = ({ size = 80 }: { size?: number }) => (
  <View
    style={{
      width: size,
      height: size,
      borderRadius: 12,
      backgroundColor: '#F0F0F0',
      justifyContent: 'center',
      alignItems: 'center',
    }}
  >
    <svg width={size * 0.4} height={size * 0.4} viewBox="0 0 24 24" fill="none">
      <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V8C3 7.46957 3.21071 6.96086 3.58579 6.58579C3.96086 6.21071 4.46957 6 5 6H19C19.5304 6 20.0391 6.21071 20.4142 6.58579C20.7893 6.96086 21 7.46957 21 8V15Z" stroke="#CCCCCC" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 11C12.8284 11 13.5 10.3284 13.5 9.5C13.5 8.67157 12.8284 8 12 8C11.1716 8 10.5 8.67157 10.5 9.5C10.5 10.3284 11.1716 11 12 11Z" stroke="#CCCCCC" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  </View>
);
