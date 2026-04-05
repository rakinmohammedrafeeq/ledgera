import { useState } from 'react';

const PRIMARY_SRC = '/gold-logo.png';
const FALLBACK_SRC = '/ledgera-icon.svg';

const sizeMap = {
  small: { width: '32px', height: '32px' },
  medium: { width: '40px', height: '40px' },
  large: { width: '80px', height: '80px' },
  xlarge: { width: '96px', height: '96px' },
};

export default function Logo({ size = 'medium', className = '', alt = 'Ledgera Logo' }) {
  const [useFallback, setUseFallback] = useState(false);
  const src = useFallback ? FALLBACK_SRC : PRIMARY_SRC;
  const dims = sizeMap[size] || sizeMap.medium;

  return (
    <img
      src={src}
      alt={alt}
      className={`logo ${className}`.trim()}
      width={parseInt(dims.width, 10)}
      height={parseInt(dims.height, 10)}
      style={{
        width: dims.width,
        height: dims.height,
        objectFit: 'contain',
        display: 'block',
        flexShrink: 0,
      }}
      onError={() => setUseFallback(true)}
    />
  );
}
